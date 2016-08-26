package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.hives.DefaultHive;
import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.HtmlWebResource;
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
	private final Set<Locator> visited = new HashSet<>();
	
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
			List<Link> links = doTask(task, distanceLimit);
			if (links != null && links.size() > 0) {
				addNewTasks(links, task.getDistance() + 1);
			}
		}
	}
	
	protected List<Link> doTask(Task task, int distanceLimit) {
		List<Link> links = null;
		if (hasVisited(task.getLocation())) {
			task.setStatus(Task.Status.SKIPPED);
		} else {
			try {
				links = visit(task.getLocation(), task.getDistance(), distanceLimit);
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
	
	protected void addNewTasks(List<Link> links, int distance) {
		for (int i = 0; i < links.size(); ++i) {
			this.tasks.add(i, new Task(links.get(i).getLocation(), distance));
		}
	}
	
	protected List<Link> visit(Locator location, int distance, int distanceLimit) throws Exception {
		List<Link> links = null;
		addToHistory(location);
		WebResource resource = retrieveResource(location);
		if (resource instanceof HtmlWebResource) {
			links = parseHtmlResource((HtmlWebResource)resource, distance, distanceLimit);
		}
		storeResource(resource, links);
		return links;
	}
	
	protected List<Link> parseHtmlResource(HtmlWebResource resource, int distance, int distanceLimit) {
		List<Link> links = new ArrayList<>();
		for (Link link: resource.getComponentLinks()) {
			if (canVisit(link.getLocation())) {
				links.add(link);
			}
		}
		if (distance < distanceLimit) {
			for (Link link : resource.getExternalLinks()) {
				if (canVisit(link.getLocation())) {
					links.add(link);
				}
			}
		}
		return links;
	}
	
	protected WebResource retrieveResource(Locator location) throws Exception {
		return this.downloader.download(location);
	}

	protected void storeResource(WebResource resource, List<Link> linkTargets) throws IOException {
		this.hive.store(resource, linkTargets);
	}
	
	protected boolean canVisit(Locator location) {
		for (WebSite site : this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
	}

	protected void addToHistory(Locator location) {
		this.visited.add(location);
	}

	protected boolean hasVisited(Locator location) {
		return this.visited.contains(location);
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
