package com.github.i49.bee.core;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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
	private VisitMap registry;
	private final List<BeeEventListener> listeners = new ArrayList<>();
	
	public Bee() {
		addDefaultEventListeners();
	}

	public List<Trip> getTrips() {
		return trips;
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
			makeAllTrips();
			unprepareAfterAllTrips();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void prepareBeforeAllTrips() throws IOException {
		if (this.hive == null) {
			this.hive = createDefaultHive();
		}
		this.hive.open();
		this.registry = new VisitMap();
	}
	
	protected void makeAllTrips() throws Exception {
		try (WebDownloader downloader = createWebDownloader(this.hive)) {
			BeeAsVisitor visitor = asVisitor();
			visitor.downloader = downloader;
			RootTask task = createRootTask(visitor);
			task.doTask();
		}
	}

	protected void unprepareAfterAllTrips() throws IOException {
		this.hive.close();
	}
	
	protected RootTask createRootTask(Visitor visitor) {
		RootTask root = new RootTask();
		root.setVisitor(visitor);
		for (Trip trip : this.trips) {
			root.addSubtask(new TripTask(trip));
		}
		return root;
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
	
	protected BeeAsVisitor asVisitor() {
		return new BeeAsVisitor();
	}
	
	private class BeeAsVisitor implements Visitor {

		private WebDownloader downloader;
		private Trip currentTrip;
		
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
			if (distance > getCurrentTrip().getDistanceLimit()) {
				return false;
			}
			return canVisit(location);
		}

		@Override
		public boolean hasDone(Locator location) {
			Visit record = getVisitMap().findVisit(location);
			if (record == null) {
				return false;
			}
			return getCurrentTrip().hasDone(record);
		}
		
		@Override
		public void addDone(Visit record) {
			getCurrentTrip().addDone(record);
		}

		@Override
		public Trip getCurrentTrip() {
			return currentTrip;
		}

		@Override
		public void setCurrentTrip(Trip trip) {
			this.currentTrip = trip;
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
			return registry;
		}

		@Override
		public void notifyEvent(Consumer<BeeEventListener> consumer) {
			for (BeeEventListener listener : listeners) {
				consumer.accept(listener);
			}
		}
	}
}
