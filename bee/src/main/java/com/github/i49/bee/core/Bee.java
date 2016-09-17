package com.github.i49.bee.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.hives.DefaultHive;
import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.hives.Storage;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.CachingWebDownloader;

/**
 * Bee who visits web sites.
 */
public class Bee {
	
	private static final Log log = LogFactory.getLog(Bee.class);
	
	private final List<Trip> trips = new ArrayList<>();
	private final List<WebSite> sites = new ArrayList<>();
	
	private Hive hive; 
	private WebDownloader downloader;
	private VisitMap visitMap;
	private final List<BeeEventHandler> handlers = new ArrayList<>();
	
	public Bee() {
		addDefaultEventHandlers();
	}

	public List<Trip> getTrips() {
		return trips;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public List<BeeEventHandler> getEventHandlers() {
		return handlers;
	}
	
	public void launch() {
		log.debug("Bee launched.");
		try {
			makeAllTrips();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void makeAllTrips() throws Exception {
		this.visitMap = new VisitMap();
		try (Hive hive = openHive(); WebDownloader downloader = createWebDownloader(hive)) {
			runTasks();
		}
	}

	protected void runTasks() {
		for (Trip trip : this.trips) {
			TripTask task = new TripTask(trip);
			BeeAsVisitor visitor = createVisitor(trip.getDistanceLimit());
			task.setVisitor(visitor);
			task.run();
		}
	}
	
	protected Hive openHive() throws IOException {
		if (this.hive == null) {
			this.hive = new DefaultHive();
		}
		this.hive.open();
		return this.hive;
	}

	protected WebDownloader createWebDownloader(Hive hive) throws IOException {
		Path pathToCache = getCacheDirectoryForHive(hive);
		this.downloader = new CachingWebDownloader(pathToCache);
		return downloader;
	}
	
	private static Path getCacheDirectoryForHive(Hive hive) {
		Storage storage = hive.getStorage();
		if (storage.isDirectory()) {
			return hive.getBasePath().resolve(".bee");
		} else {
			return Paths.get(hive.getBasePath().toString() + ".bee");
		}
	}
	
	protected void addDefaultEventHandlers() {
		this.handlers.add(new DefaultReporter());
	}
	
	protected BeeAsVisitor createVisitor(int distanceLimit) {
		return new BeeAsVisitor(distanceLimit);
	}

	/**
	 * A visitor making one trip.
	 */
	private class BeeAsVisitor implements Visitor {

		private final int distanceLimit;
		private final Set<Visit> done = new HashSet<>();
		
		public BeeAsVisitor(int distanceLimit) {
			this.distanceLimit = distanceLimit;
		}
		
		@Override
		public boolean canVisit(Locator location) {
			for (WebSite site : sites) {
				if (site.contains(location)) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		public boolean canVisit(Locator location, int distance) {
			if (distance > this.distanceLimit) {
				return false;
			}
			return canVisit(location);
		}

		@Override
		public boolean hasDone(Locator location) {
			Visit visit = getVisitMap().findVisit(location);
			if (visit == null) {
				return false;
			}
			return this.done.contains(visit);
		}
		
		@Override
		public void addDone(Visit visit) {
			if (visit != null) {
				this.done.add(visit);
			}
		}

		@Override
		public WebDownloader getDownloader() {
			return downloader;
		}

		@Override
		public Hive getHive() {
			return hive;
		}

		@Override
		public VisitMap getVisitMap() {
			return visitMap;
		}

		@Override
		public void notifyEvent(Consumer<BeeEventHandler> consumer) {
			handlers.stream().forEach(consumer);
		}
	}
}
