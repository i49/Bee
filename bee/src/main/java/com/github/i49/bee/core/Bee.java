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
	
	private final LinkedList<Task> tasks = new LinkedList<>();
	private final Set<URI> visited = new HashSet<URI>();
	
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
		this.tasks.clear();
		this.visited.clear();
		try (WebDownloader downloader = new WebDownloader()) {
			this.downloader = downloader;
			makeAllTrips(this.seeds);
			this.downloader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void makeAllTrips(List<Seed> seeds) {
		for (Seed seed: seeds) {
			makeTrip(seed);
		}
	}
	
	protected void makeTrip(Seed seed) {
		this.tasks.clear();
		URI location;
		try {
			location = new URI(seed.getLocation());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}
		this.tasks.add(new Task(location));
		doAllTasks(seed.getDistanceLimit());
	}
	
	protected void doAllTasks(int distanceLimit) {
		while (!this.tasks.isEmpty()) {
			Task task = this.tasks.removeFirst();
			doTask(task, distanceLimit);
		}
	}
	
	protected void doTask(Task task, int distanceLimit) {
		if (hasVisited(task.getLocation())) {
			task.setStatus(Task.Status.SKIPPED);
		} else {
			try {
				List<URI> found = visit(task.getLocation(), task.getDistance(), distanceLimit);
				if (found != null && found.size() > 0) {
					addNewTasks(found, task.getDistance() + 1);
				}
				task.setStatus(Task.Status.DONE);
			} catch (IOException | SAXException e) {
				task.setStatus(Task.Status.FAILED);
			}
		}
		reportTaskResult(task);
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
		return found;
	}
	
	protected List<URI> parseHtmlResource(HtmlWebResource resource, int distance, int distanceLimit) {
		if (distance >= distanceLimit) {
			return null;
		}
		List<URI> found = new ArrayList<>();
		for (URI link: resource.getOutboundLinks()) {
			if (canVisit(link)) {
				found.add(link);
			}
		}
		return found;
	}
	
	protected WebResource getResource(URI location) throws IOException, SAXException {
		return this.downloader.download(location);
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
	
	protected void reportTaskResult(Task task) {
		String status = null;
		switch (task.getStatus()) {
		case DONE:
			status = "+";
			break;
		case FAILED:
			status = "!";
			break;
		default:
			status = " ";
			break;
		}
		log.info("[" + task.getDistance() + "]" + status + task.getLocation().toString());
	}
}
