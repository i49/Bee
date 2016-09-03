package com.github.i49.bee.core;

import java.io.IOException;
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
import com.github.i49.bee.web.HtmlWebResource;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebDownloader;
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
	
	private final LinkedList<Task> tasks = new LinkedList<>();

	private final Map<Locator, WebResource> retrieved = new HashMap<>();
	private final Set<Locator> done = new HashSet<>();
	private final Set<Locator> stored = new HashSet<>();
	
	private final List<BeeEventListener> listeners = new ArrayList<>();
	
	private Seed currentSeed;
	private int currentDistance; 
	
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
		this.retrieved.clear();
	}
	
	protected void unprepareAfterAllTrips() throws IOException {
		this.hive.close();
	}
	
	protected void makeAllTrips(List<Seed> seeds) {
		try (WebDownloader downloader = new WebDownloader()) {
			this.downloader = downloader;
			for (Seed seed : seeds) {
				makeTrip(seed);
			}
			this.downloader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void makeTrip(Seed seed) {
		prepareBeforeTrip(seed);
		doAllTasks();
		unprepareAfterTrip();
	}
	
	protected void prepareBeforeTrip(Seed seed) {
		this.done.clear();
		this.currentSeed = seed;
		this.currentDistance = 0;
		this.tasks.clear();
		Locator location = Locator.parse(seed.getLocation());
		if (location != null) {
			this.tasks.add(new Task(location));
		}
	}
	
	protected void unprepareAfterTrip() {
	}
	
	protected void doAllTasks() {
		while (!this.tasks.isEmpty()) {
			Task task = this.tasks.removeFirst();
			int distance = task.getDistance();
			Collection<ResourceMetadata> links = doTask(task);
			if (links != null && links.size() > 0 && distance < currentSeed.getDistanceLimit()) {
				addNewTasks(links, distance + 1);
			}
		}
	}
	
	protected Collection<ResourceMetadata> doTask(Task task) {
		this.currentDistance = task.getDistance();
		final boolean visitNew = (currentDistance < currentSeed.getDistanceLimit());
		if (hasDone(task.getLocation())) {
			return null;
		} else {
			return visit(task.getLocation(), visitNew);
		}
	}
	
	protected void addNewTasks(Collection<ResourceMetadata> links, int distance) {
		int pos = 0;
		for (ResourceMetadata metadata : links) {
			Task task = new Task(metadata.getLocation(), distance);
			this.tasks.add(pos++, task);
		}
	}
	
	protected Collection<ResourceMetadata> visit(Locator location, boolean visitNew) {
		Collection<ResourceMetadata> links = null;
		WebResource resource = retrieveResource(location, false);
		if (resource == null) {
			return null;
		}
		if (resource instanceof HtmlWebResource) {
			links = parseLinkSource((HtmlWebResource)resource, visitNew);
		} else {
			storeResource(resource, null, false);
		}
		return links;
	}
	
	protected Collection<ResourceMetadata> parseLinkSource(HtmlWebResource resource, boolean visitNew) {
		Map<Locator, ResourceMetadata> children = visitChildResources(resource);
		Map<Locator, ResourceMetadata> neighbors = searchNeighbors(resource, visitNew);
		Map<Locator, ResourceMetadata> all = new HashMap<>();
		all.putAll(children);
		all.putAll(neighbors);
		storeResource(resource, all, false);
		return neighbors.values();
	}

	protected Map<Locator, ResourceMetadata> visitChildResources(LinkSource resource) {
		Map<Locator, ResourceMetadata> children = new LinkedHashMap<>();
		for (Link link: resource.getComponentLinks()) {
			WebResource child = visitChildResource(link.getLocation());
			if (child != null) {
				children.put(link.getLocation(), child.getMetadata());
			}
		}
		return children;
	}
	
	protected WebResource visitChildResource(Locator location) {
		if (canVisit(location)) {
			WebResource child = getRetrieved(location);
			if (child == null) {
				child= retrieveResource(location, true);
			}
			if (child != null) {
				storeResource(child, null, true);
			}
			return child;
		} else {
			return null;
		}
	}
	
	protected Map<Locator, ResourceMetadata> searchNeighbors(LinkSource resource, boolean visitNew) {
		Map<Locator, ResourceMetadata> neighbors = new LinkedHashMap<>();
		for (Link link: resource.getExternalLinks()) {
			WebResource neighbor = visitExternalResource(link.getLocation(), visitNew);
			if (neighbor != null) {
				neighbors.put(link.getLocation(), neighbor.getMetadata());
			}
		}
		return neighbors;
	}
	
	protected WebResource visitExternalResource(Locator location, boolean visitNew) {
		if (canVisit(location)) {
			WebResource child = getRetrieved(location);
			if (child == null && visitNew) {
				child = retrieveResource(location, true);
			}
			return child;
		} else {
			return null;
		}
	}
	
	protected WebResource retrieveResource(Locator location, boolean subordinate) {
		ResourceStatus status = null;
		try {
			WebResource resource = getRetrieved(location);
			if (resource != null) {
				status = ResourceStatus.CACHE;
			} else {
				resource = this.downloader.download(location);
				addRetrieved(location, resource);
				status = ResourceStatus.SUCCESS;
			}
			return resource;
		} catch (Exception e) {
			status = ResourceStatus.FAIL;
			return null;
		} finally {
			notifyResourceEvent(location, subordinate, ResourceOperation.GET, status);
		}
	}

	protected void storeResource(WebResource resource, Map<Locator, ResourceMetadata> links, boolean subordinate) {
		final Locator location = resource.getMetadata().getLocation();
		ResourceStatus status = null;
		try {
			if (hasStored(location)) {
				status = ResourceStatus.SKIP;
			} else {
				this.hive.store(resource, links);
				this.done.add(location);
				this.stored.add(location);
				status = ResourceStatus.SUCCESS;
			}
		} catch (IOException e) {
			status = ResourceStatus.FAIL;
		} finally {
			if (status == null) {
				log.debug("xxx");
			}
			notifyResourceEvent(location, subordinate, ResourceOperation.STORE, status);
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
		return done.contains(location);
	}
	
	protected boolean hasStored(Locator location) {
		return this.stored.contains(location);
	}

	protected WebResource getRetrieved(Locator location) {
		return this.retrieved.get(location);
	}

	protected void addRetrieved(Locator location, WebResource resource) {
		this.retrieved.put(location, resource);
		this.retrieved.put(resource.getMetadata().getLocation(), resource);
	}
	
	protected void notifyResourceEvent(Locator location, boolean subordinate, ResourceOperation operation, ResourceStatus status) {
		final int distance = subordinate ? this.currentDistance + 1 : this.currentDistance;
		ResourceEvent e = new ResourceEvent(location, operation);
		e.setDistance(distance);
		e.setSubordinate(subordinate);
		e.setStatus(status);
		fireResourceEvent(e);
	}
	
	protected void fireResourceEvent(ResourceEvent e) {
		for (BeeEventListener listener : this.listeners) {
			listener.handleResourceEvent(e);
		}
	}
	
	protected Hive createDefaultHive() {
		return new DefaultHive();
	}
	
	protected void addDefaultEventListeners() {
		this.listeners.add(new DefaultReporter());
	}
}
