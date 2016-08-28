package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	private final Map<Locator, ResourceMetadata> visited = new HashMap<>();

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
		this.visited.clear();
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
			List<ResourceMetadata> links = doTask(task);
			if (links != null && links.size() > 0) {
				addNewTasks(links, task.getDistance() + 1);
			}
		}
	}
	
	protected List<ResourceMetadata> doTask(Task task) {
		this.currentDistance = task.getDistance();
		final boolean visitNew = (currentDistance < currentSeed.getDistanceLimit());
		List<ResourceMetadata> links = null;
		if (!hasVisited(task.getLocation())) {
			links = visit(task.getLocation(), visitNew);
		}
		return links;
	}
	
	protected void addNewTasks(List<ResourceMetadata> links, int distance) {
		for (int i = 0; i < links.size(); ++i) {
			this.tasks.add(i, new Task(links.get(i).getFinalLocation(), distance));
		}
	}
	
	protected List<ResourceMetadata> visit(Locator location, boolean visitNew) {
		List<ResourceMetadata> links = null;
		WebResource resource = retrieveResource(location, false);
		if (resource == null) {
			return null;
		}
		addToHistory(resource);
		if (resource instanceof HtmlWebResource) {
			links = parseHtmlResource((HtmlWebResource)resource, visitNew);
		} else {
			storeResource(resource, null, false);
		}
		return links;
	}
	
	protected List<ResourceMetadata> parseHtmlResource(HtmlWebResource resource, boolean visitNew) {
		List<ResourceMetadata> internal = visitInternalResources(resource);
		List<ResourceMetadata> external = visitExternalResources(resource, visitNew);
		List<ResourceMetadata> all = new ArrayList<>(internal);
		all.addAll(external);
		storeResource(resource, all, false);
		return external;
	}

	protected List<ResourceMetadata> visitInternalResources(HtmlWebResource resource) {
		List<ResourceMetadata> result = new ArrayList<>();
		for (Link link: resource.getComponentLinks()) {
			ResourceMetadata meta = visitInternalResource(link.getLocation());
			if (meta != null) {
				result.add(meta);
			}
		}
		return result;
	}
	
	protected ResourceMetadata visitInternalResource(Locator location) {
		ResourceMetadata meta = null;
		if (canVisit(location)) {
			if (hasDone(location)) {
				meta = getVisited(location);
			} else {
				WebResource resource = retrieveResource(location, true);
				if (resource != null) {
					storeResource(resource, null, true);
					meta = addToHistory(resource);
				}
			}
		}
		return meta;
	}
	
	protected List<ResourceMetadata> visitExternalResources(HtmlWebResource resource, boolean visitNew) {
		List<ResourceMetadata> result = new ArrayList<>();
		for (Link link: resource.getExternalLinks()) {
			ResourceMetadata meta = visitExternalResource(link.getLocation(), visitNew);
			if (meta != null) {
				result.add(meta);
			}
		}
		return result;
	}
	
	protected ResourceMetadata visitExternalResource(Locator location, boolean visitNew) {
		ResourceMetadata meta = null;
		if (canVisit(location)) {
			if (hasVisited(location)) {
				meta = getVisited(location);
			} else if (visitNew) {
				WebResource resource = retrieveResource(location, true);
				if (resource != null) {
					meta = resource.getMetadata();
				}
			}
		}
		return meta;
	}
	
	protected WebResource retrieveResource(Locator location, boolean subordinate) {
		ResourceStatus status = null;
		try {
			WebResource resource = this.downloader.download(location);
			status = ResourceStatus.SUCCESS;
			return resource;
		} catch (Exception e) {
			status = ResourceStatus.FAIL;
			return null;
		} finally {
			notifyResourceEvent(location, subordinate, ResourceOperation.GET, status);
		}
	}

	protected void storeResource(WebResource resource, List<ResourceMetadata> links, boolean subordinate) {
		final Locator location = resource.getMetadata().getFinalLocation();
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

	protected boolean hasVisited(Locator location) {
		return (getVisited(location) != null);
	}
	
	protected ResourceMetadata getVisited(Locator location) {
		return this.visited.get(location);
	}

	protected ResourceMetadata addToHistory(WebResource resource) {
		ResourceMetadata meta = resource.getMetadata();
		this.visited.put(meta.getLocation(), meta);
		if (meta.isRedirected()) {
			this.visited.put(meta.getRedirectLocation(), meta);
		}
		return meta;
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
