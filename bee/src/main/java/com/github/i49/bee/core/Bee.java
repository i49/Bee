package com.github.i49.bee.core;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Deque;
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
	
	private final List<SeedPage> seeds = new ArrayList<>();
	private final List<WebSite> sites = new ArrayList<>();
	
	private WebDownloader downloader;
	
	private final Deque<Place> placesToVisit = new LinkedList<>();
	private final Set<URL> history = new HashSet<URL>();
	
	public Bee() {
	}

	public List<SeedPage> getSeeds() {
		return seeds;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public void visitAll() {
		log.debug("Starting visitAll()");
		this.placesToVisit.clear();
		this.history.clear();
		try (WebDownloader downloader = new WebDownloader()) {
			this.downloader = downloader;
			visitAllSeeds(this.seeds);
			this.downloader = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void visitAllSeeds(List<SeedPage> seeds) {
		for (SeedPage seed: seeds) {
			visitSeed(seed);
		}
	}
	
	protected void visitSeed(SeedPage seed) {
		try {
			URL location = new URL(seed.getLocation());
			visit(location, seed.getDistance());
			while (!this.placesToVisit.isEmpty()) {
				Place place = this.placesToVisit.removeFirst();
				visit(place.getLocation(), place.getDistance());
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	protected void visit(URL location, int distance) {
		addToHistory(location);
		WebResource resource = fetch(location);
		if (resource instanceof HtmlWebResource) {
			handleHtmlResource((HtmlWebResource)resource, distance);
		}
	}
	
	protected void handleHtmlResource(HtmlWebResource resource, int distance) {
		if (distance > 0) {
			final int nextDistance = distance - 1; 
			for (URL link: resource.getOutboundLinks()) {
				planToVisit(resource.getLocation(), link, nextDistance);
			}
		}
	}
	
	protected void planToVisit(URL current, URL next, int distance) {
		if (!hasVisited(next) && canVisit(next)) {
			this.placesToVisit.add(new Place(next, distance));
		}
	}
	
	protected WebResource fetch(URL location) {
		log.debug("Fetching " + location.toString());
		try {
			WebResource resource = this.downloader.download(location);
			return resource;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected boolean canVisit(URL location) {
		for (WebSite site: this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
	}

	protected void addToHistory(URL location) {
		this.history.add(location);
	}

	protected boolean hasVisited(URL location) {
		return this.history.contains(location);
	}
}
