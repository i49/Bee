
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
import com.github.i49.bee.hives.HiveException;
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
	
	public void launch() throws BeeException {
		log.debug("Bee launched.");
		try {
			History history = new History();
			makeAllTrips(history);
			rewriteLinks(history);
		} catch (Exception e) {
			throw new BeeException(e);
		}
	}
	
	protected void makeAllTrips(History history) throws Exception {
		try (Hive hive = openHive(); WebDownloader downloader = createWebDownloader(hive)) {
			for (Trip trip : this.trips) {
				Tripper tripper = new BeeAsTripper(trip, downloader, hive, history);
				tripper.makeTrip();
			}
		}
	}

	protected void rewriteLinks(History history) throws HiveException {
	}
	
	protected Hive openHive() throws IOException {
		if (this.hive == null) {
			this.hive = new DefaultHive();
		}
		this.hive.open();
		return this.hive;
	}

	protected WebDownloader createWebDownloader(Hive hive) {
		Path pathToCache = getCacheDirectoryForHive(hive);
		WebDownloader downloader = new CachingWebDownloader(pathToCache);
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
		this.handlers.add(new BasicConsoleLogger());
	}
	
	private class BeeAsTripper extends Tripper {

		public BeeAsTripper(Trip trip, WebDownloader downloader, Hive hive, History history) {
			super(trip, downloader, hive, history);
		}
		
		@Override
		protected boolean canVisit(Locator location) {
			for (WebSite site : sites) {
				if (site.contains(location)) {
					return true;
				}
			}
			return false;
		}
		
		@Override
		protected void report(Consumer<BeeEventHandler> action) {
			handlers.stream().forEach(action);
		}
	}
}
