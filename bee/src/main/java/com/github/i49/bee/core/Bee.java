package com.github.i49.bee.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.hives.DefaultHive;
import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.hives.Storage;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkProvidingResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.CachingWebDownloader;
import com.github.i49.bee.web.WebResource;

/**
 * Bee who visits web sites.
 */
public class Bee {
	
	private static final Log log = LogFactory.getLog(Bee.class);
	
	private final List<Seed> seeds = new ArrayList<>();
	private final List<WebSite> sites = new ArrayList<>();
	
	private WebDownloader downloader;
	private Hive hive; 
	
	private final LinkedList<ResourceTask> tasks = new LinkedList<>();

	private ResourceRegistry registry;
	private final Set<ResourceRecord> done = new HashSet<>();
	
	private final List<BeeEventListener> listeners = new ArrayList<>();
	
	public Bee() {
		addDefaultEventListeners();
	}

	public List<Seed> getSeeds() {
		return seeds;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public List<BeeEventListener> getEventListeners() {
		return listeners;
	}
	
	public void launch() {
		log.debug("Bee launched.");
		try {
			prepareBeforeAllTrips();
			makeAllTrips(this.seeds);
			unprepareAfterAllTrips();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void prepareBeforeAllTrips() throws IOException {
		if (this.hive == null) {
			this.hive = createDefaultHive();
		}
		this.hive.open();
		this.tasks.clear();
		this.registry = new ResourceRegistry();
	}
	
	protected void unprepareAfterAllTrips() throws IOException {
		this.hive.close();
	}
	
	protected void makeAllTrips(List<Seed> seeds) {
		try (WebDownloader downloader = createWebDownloader(this.hive)) {
			this.downloader = downloader;
			for (Seed seed : seeds) {
				makeTrip(seed);
			}
			this.downloader = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void makeTrip(Seed seed) {
		prepareBeforeTrip(seed);
		doAllTasks(seed.getDistanceLimit());
		unprepareAfterTrip();
	}
	
	protected void prepareBeforeTrip(Seed seed) {
		this.done.clear();
		this.tasks.clear();
		Locator location = Locator.parse(seed.getLocation());
		if (location != null) {
			this.tasks.add(new ResourceTask(location, 0));
		}
	}
	
	protected void unprepareAfterTrip() {
	}
	
	protected void doAllTasks(int distanceLimit) {
		while (!this.tasks.isEmpty()) {
			ResourceTask task = this.tasks.removeFirst();
			final int distance = task.getDistance();
			final boolean visitNew = (distance < distanceLimit);
			Collection<ResourceMetadata> links = doTask(task, visitNew);
			if (links != null && links.size() > 0 && visitNew) {
				addNewTasks(links, distance + 1);
			}
		}
	}
	
	protected Collection<ResourceMetadata> doTask(ResourceTask task, boolean visitNew) {
		if (hasDone(task.getLocation())) {
			return null;
		} else {
			return visit(task, visitNew);
		}
	}
	
	protected void addNewTasks(Collection<ResourceMetadata> links, int distance) {
		int pos = 0;
		for (ResourceMetadata metadata : links) {
			ResourceTask task = new ResourceTask(metadata.getLocation(), distance);
			this.tasks.add(pos++, task);
		}
	}
	
	protected Collection<ResourceMetadata> visit(ResourceTask task, boolean visitNew) {
		Collection<ResourceMetadata> links = null;
		retrieveResource(task);
		WebResource resource = task.getResource();
		if (resource == null) {
			return null;
		}
		if (resource instanceof LinkProvidingResource) {
			links = parseLinkProvidingResource(task, (LinkProvidingResource)resource, visitNew);
		} else {
			storeResource(task, null);
		}
		done(task.getRecord());
		return links;
	}
	
	protected Collection<ResourceMetadata> parseLinkProvidingResource(ResourceTask task, LinkProvidingResource resource, boolean visitNew) {
		Map<Locator, ResourceMetadata> depends = visitDependencies(task, resource);
		Map<Locator, ResourceMetadata> neighbors = searchNeighbors(task, resource, visitNew);
		Map<Locator, ResourceMetadata> all = new HashMap<>();
		all.putAll(depends);
		all.putAll(neighbors);
		storeResource(task, all);
		return neighbors.values();
	}
	
	protected Map<Locator, ResourceMetadata> visitDependencies(ResourceTask task, LinkProvidingResource resource) {
		Map<Locator, ResourceMetadata> depends = new LinkedHashMap<>();
		for (Link link: resource.getDependencyLinks()) {
			ResourceTask subtask = task.createSubtask(link.getLocation());
			ResourceMetadata metadata = visitDependency(subtask);
			if (metadata != null) {
				depends.put(task.getLocation(), metadata);
			}
		}
		return depends;
	}
	
	protected ResourceMetadata visitDependency(ResourceTask task) {
		Locator location = task.getLocation();
		if (canVisit(location)) {
			if (hasDone(location)) {
				ResourceRecord record = registry.find(location);
				return record.getMetadata();
			} else {
				retrieveResource(task);
				storeResource(task, null);
				return task.getResource().getMetadata();
			}
		} else {
			return null;
		}
	}
	
	protected Map<Locator, ResourceMetadata> searchNeighbors(ResourceTask task, LinkProvidingResource resource, boolean visitNew) {
		Map<Locator, ResourceMetadata> neighbors = new LinkedHashMap<>();
		for (Link link: resource.getExternalLinks()) {
			ResourceTask subtask = task.createSubtask(link.getLocation());
			ResourceMetadata neighbor = searchNeighbor(subtask, visitNew);
			if (neighbor != null) {
				neighbors.put(link.getLocation(), neighbor);
			}
		}
		return neighbors;
	}
	
	protected ResourceMetadata searchNeighbor(ResourceTask task, boolean visitNew) {
		Locator location = task.getLocation();
		if (canVisit(location)) {
			ResourceRecord record = registry.find(location);
			if (record != null) {
				return record.getMetadata();
			} else if (visitNew) {
				retrieveResource(task);
				return task.getResource().getMetadata();
			} else {
				return null;
			}
		} else {
			return null;
		}
	}
	
	protected void retrieveResource(ResourceTask task) {
		task.setPhase(ResourceTaskPhase.GET);
		try {
			Locator location = task.getLocation();
			WebResource resource =  this.downloader.download(location);
			task.setResource(resource);
			task.setRecord(recordResource(location, resource));
			notifyTaskEvent(task);
		} catch (WebException e) {
			notifyTaskFailure(task, e);
		}
	}

	protected void storeResource(ResourceTask task, Map<Locator, ResourceMetadata> links) {
		task.setPhase(ResourceTaskPhase.STORE);
		WebResource resource = task.getResource();	
		if (resource == null) {
			return;
		}
		ResourceRecord record = task.getRecord();
		if (record.isStored()) {
			return;
		}
		try {
			this.hive.store(resource, links);
			record.setStored();
			notifyTaskEvent(task);
		} catch (IOException e) {
			notifyTaskFailure(task, e);
		}
	}
	
	protected boolean canVisit(Locator location) {
		for (WebSite site : this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
	}
	
	protected boolean hasDone(Locator location) {
		ResourceRecord record = this.registry.find(location);
		if (record == null) {
			return false;
		}
		return this.done.contains(record);
	}
	
	protected void done(ResourceRecord record) {
		if (record != null) {
			this.done.add(record);
		}
	}
	
	protected ResourceRecord recordResource(Locator location, WebResource resource) {
		return this.registry.register(location, resource.getMetadata());
	}
	
	protected void notifyTaskEvent(ResourceTask task) {
		for (BeeEventListener listener : this.listeners) {
			listener.handleTaskEvent(task);
		}
	}
	
	protected void notifyTaskFailure(ResourceTask task, Exception cause) {
		task.setCause(cause);
		for (BeeEventListener listener : this.listeners) {
			listener.handleTaskFailure(task);
		}
	}

	protected Hive createDefaultHive() {
		return new DefaultHive();
	}
	
	protected WebDownloader createWebDownloader(Hive hive) throws IOException {
		Path pathToCache = getCacheDirectoryForHive(hive);
		return new CachingWebDownloader(pathToCache);
	}
	
	private static Path getCacheDirectoryForHive(Hive hive) {
		Storage storage = hive.getStorage();
		if (storage.isDirectory()) {
			return hive.getBasePath().resolve(".bee");
		} else {
			return Paths.get(hive.getBasePath().toString() + ".bee");
		}
	}
	
	protected void addDefaultEventListeners() {
		this.listeners.add(new DefaultReporter());
	}
}
