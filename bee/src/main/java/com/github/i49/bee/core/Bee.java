package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
	
	private BeeStatistics stats;
	private Reporter reporter;

	public Bee() {
	}

	public List<Seed> getSeeds() {
		return seeds;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public void launch() {
		log.debug("Bee launched.");
		try {
			prepareTrips();
			makeAllTrips(this.seeds);
			finishTrips();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void prepareTrips() throws IOException {
		this.stats = new BeeStatistics();
		if (this.reporter == null) {
			this.reporter = createDefaultReporter();
		}
		if (this.hive == null) {
			this.hive = createDefaultHive();
		}
		this.hive.open();
		this.tasks.clear();
		this.visited.clear();
	}
	
	protected void finishTrips() throws IOException {
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
		this.reporter.reportTotalResult(this.stats);
	}
	
	protected void makeTrip(Seed seed) {
		Locator location = Locator.parse(seed.getLocation());
		if (location == null) {
			return;
		}
		this.tasks.clear();
		this.tasks.add(new Task(location));
		doAllTasks(seed.getDistanceLimit());
	}
	
	protected void doAllTasks(int distanceLimit) {
		while (!this.tasks.isEmpty()) {
			Task task = this.tasks.removeFirst();
			List<ResourceMetadata> links = doTask(task, distanceLimit);
			if (links != null && links.size() > 0) {
				addNewTasks(links, task.getDistance() + 1);
			}
		}
	}
	
	protected List<ResourceMetadata> doTask(Task task, int distanceLimit) {
		final boolean visitNew = (task.getDistance() < distanceLimit);
		List<ResourceMetadata> links = null;
		if (hasVisited(task.getLocation())) {
			task.setStatus(Task.Status.SKIPPED);
		} else {
			try {
				links = visit(task.getLocation(), visitNew);
				task.setStatus(Task.Status.DONE);
				this.stats.successes++;
			} catch (Exception e) {
				log.error(e.getMessage());
				log.debug("Exception: ", e);
				task.setStatus(Task.Status.FAILED);
				this.stats.failures++;
			}
		}
		this.reporter.reportTaskResult(task);
		return links;
	}
	
	protected void addNewTasks(List<ResourceMetadata> links, int distance) {
		for (int i = 0; i < links.size(); ++i) {
			this.tasks.add(i, new Task(links.get(i).getFinalLocation(), distance));
		}
	}
	
	protected List<ResourceMetadata> visit(Locator location, boolean visitNew) throws Exception {
		List<ResourceMetadata> links = null;
		WebResource resource = retrieveResource(location);
		addToHistory(resource);
		if (resource instanceof HtmlWebResource) {
			links = parseHtmlResource((HtmlWebResource)resource, visitNew);
		} else {
			storeResource(resource);
		}
		return links;
	}
	
	protected List<ResourceMetadata> parseHtmlResource(HtmlWebResource resource, boolean visitNew) throws IOException {
		List<ResourceMetadata> internal = visitInternalResources(resource);
		List<ResourceMetadata> external = visitExternalResources(resource, visitNew);
		storeResource(resource, internal, external);
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
			if (hasVisited(location)) {
				meta = getVisited(location);
			} else {
				try {
					WebResource resource = retrieveResource(location);
					storeResource(resource);
					meta = addToHistory(resource);
				} catch (Exception e) {
					e.printStackTrace();
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
				try {
					WebResource resource = retrieveResource(location);
					meta = resource.getMetadata();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return meta;
	}

	protected WebResource retrieveResource(Locator location) throws Exception {
		return this.downloader.download(location);
	}

	protected void storeResource(WebResource resource) throws IOException {
		this.hive.store(resource, null);
	}
	
	protected void storeResource(WebResource resource, List<ResourceMetadata> internal, List<ResourceMetadata> external) throws IOException {
		List<ResourceMetadata> all = new ArrayList<>(internal);
		all.addAll(external);
		this.hive.store(resource, all);
	}
	
	protected boolean canVisit(Locator location) {
		for (WebSite site : this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
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
	
	protected Reporter createDefaultReporter() {
		return new DefaultReporter();
	}
	
	protected Hive createDefaultHive() {
		return new DefaultHive();
	}

	private static class BeeStatistics implements Statistics {

		private int successes;
		private int failures;
		
		@Override
		public int getSuccesses() {
			return successes;
		}

		@Override
		public int getFailures() {
			return failures;
		}
	}
}
