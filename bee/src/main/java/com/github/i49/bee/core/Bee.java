package com.github.i49.bee.core;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import com.github.i49.bee.hives.DefaultHive;
import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.HtmlWebResource;
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
	private final Set<URI> visited = new HashSet<URI>();
	
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
		prepareForTrips();
		try (WebDownloader downloader = new WebDownloader()) {
			this.downloader = downloader;
			makeAllTrips(this.seeds);
			this.downloader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.reporter.reportTotalResult(this.stats);
	}
	
	protected void prepareForTrips() {
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
	
	protected void makeAllTrips(List<Seed> seeds) {
		for (Seed seed : seeds) {
			makeTrip(seed);
		}
	}
	
	protected void makeTrip(Seed seed) {
		URI location;
		try {
			location = new URI(seed.getLocation());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		this.tasks.clear();
		this.tasks.add(new Task(location));
		doAllTasks(seed.getDistanceLimit());
	}
	
	protected void doAllTasks(int distanceLimit) {
		while (!this.tasks.isEmpty()) {
			Task task = this.tasks.removeFirst();
			List<URI> found = doTask(task, distanceLimit);
			if (found != null && found.size() > 0) {
				addNewTasks(found, task.getDistance() + 1);
			}
		}
	}
	
	protected List<URI> doTask(Task task, int distanceLimit) {
		List<URI> found = null;
		if (hasVisited(task.getLocation())) {
			task.setStatus(Task.Status.SKIPPED);
		} else {
			try {
				found = visit(task.getLocation(), task.getDistance(), distanceLimit);
				task.setStatus(Task.Status.DONE);
				this.stats.successes++;
			} catch (IOException | SAXException e) {
				log.error(e.getMessage());
				task.setStatus(Task.Status.FAILED);
				this.stats.failures++;
			}
		}
		this.reporter.reportTaskResult(task);
		return found;
	}
	
	protected void addNewTasks(List<URI> found, int distance) {
		for (int i = 0; i < found.size(); ++i) {
			this.tasks.add(i, new Task(found.get(i), distance));
		}
	}
	
	protected List<URI> visit(URI location, int distance, int distanceLimit) throws IOException, SAXException {
		List<URI> found = null;
		addToHistory(location);
		WebResource resource = getResource(location);
		if (resource instanceof HtmlWebResource) {
			found = parseHtmlResource((HtmlWebResource)resource, distance, distanceLimit);
		}
		storeResource(resource);
		return found;
	}
	
	protected List<URI> parseHtmlResource(HtmlWebResource resource, int distance, int distanceLimit) {
		if (distance >= distanceLimit) {
			return null;
		}
		List<URI> links = new ArrayList<>();
		for (URI image: resource.getImageLinks()) {
			if (canVisit(image)) {
				links.add(image);
			}
		}
		for (URI page : resource.getLinkedPages()) {
			if (canVisit(page)) {
				links.add(page);
			}
		}
		return links;
	}
	
	protected WebResource getResource(URI location) throws IOException, SAXException {
		return this.downloader.download(location);
	}

	protected void storeResource(WebResource resource) {
		this.hive.store(resource);
	}
	
	protected boolean canVisit(URI location) {
		for (WebSite site : this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
	}

	protected void addToHistory(URI location) {
		this.visited.add(location);
	}

	protected boolean hasVisited(URI location) {
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
