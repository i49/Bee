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
import com.github.i49.bee.web.HtmlResourceContent;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.Locator;
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
			List<WebResource> links = doTask(task);
			if (links != null && links.size() > 0 && distance < currentSeed.getDistanceLimit()) {
				addNewTasks(links, distance + 1);
			}
		}
	}
	
	protected List<WebResource> doTask(Task task) {
		this.currentDistance = task.getDistance();
		final boolean visitNew = (currentDistance < currentSeed.getDistanceLimit());
		if (hasDone(task.getLocation())) {
			return null;
		} else {
			return visit(task.getLocation(), visitNew);
		}
	}
	
	protected void addNewTasks(List<WebResource> links, int distance) {
		for (int i = 0; i < links.size(); ++i) {
			this.tasks.add(i, new Task(links.get(i).getFinalLocation(), distance));
		}
	}
	
	protected List<WebResource> visit(Locator location, boolean visitNew) {
		List<WebResource> links = null;
		WebResource resource = retrieveResource(location, false);
		if (resource == null) {
			return null;
		}
		if (resource.getContent() instanceof HtmlResourceContent) {
			HtmlResourceContent content = (HtmlResourceContent)(resource.getContent());
			links = parseHtmlResource(resource, content, visitNew);
		} else {
			storeResource(resource, null, false);
		}
		return links;
	}
	
	protected List<WebResource> parseHtmlResource(WebResource resource, HtmlResourceContent content, boolean visitNew) {
		List<WebResource> internal = visitInternalResources(content);
		List<WebResource> external = visitExternalResources(content, visitNew);
		List<WebResource> all = new ArrayList<>(internal);
		all.addAll(external);
		storeResource(resource, all, false);
		return external;
	}

	protected List<WebResource> visitInternalResources(HtmlResourceContent content) {
		List<WebResource> children = new ArrayList<>();
		for (Link link: content.getComponentLinks()) {
			WebResource child = visitInternalResource(link.getLocation());
			if (child != null) {
				children.add(child);
			}
		}
		return children;
	}
	
	protected WebResource visitInternalResource(Locator location) {
		if (canVisit(location)) {
			WebResource child = getRetrieved(location);
			if (child == null) {
				child= retrieveResource(location, true);
			}
			if (child != null && child.hasContent()) {
				storeResource(child, null, true);
			}
			return child;
		} else {
			return null;
		}
	}
	
	protected List<WebResource> visitExternalResources(HtmlResourceContent resource, boolean visitNew) {
		List<WebResource> result = new ArrayList<>();
		for (Link link: resource.getExternalLinks()) {
			WebResource child = visitExternalResource(link.getLocation(), visitNew);
			if (child != null) {
				result.add(child);
			}
		}
		return result;
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
				addRetrieved(resource);
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

	protected void storeResource(WebResource resource, List<WebResource> links, boolean subordinate) {
		final Locator location = resource.getFinalLocation();
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
			resource.setContent(null);
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

	protected void addRetrieved(WebResource resource) {
		this.retrieved.put(resource.getLocation(), resource);
		if (resource.isRedirected()) {
			this.retrieved.put(resource.getRedirectLocation(), resource);
		}
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
