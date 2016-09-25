
package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.hives.DefaultHive;
import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.hives.Linker;
import com.github.i49.bee.web.BasicWebDownloader;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebContentException;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.WebResource;

/**
 * Bee who visits web sites.
 */
public class Bee {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(Bee.class);
	
	private final List<Trip> trips = new ArrayList<>();
	private final List<WebSite> sites = new ArrayList<>();
	
	private Hive hive; 
	private final List<BeeEventHandler> handlers = new ArrayList<>();
	
	private int nextResourceNo;
	
	public Bee() {
		addDefaultEventHandlers();
	}

	public List<Trip> getTrips() {
		return trips;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public void addEventHandler(BeeEventHandler handler) {
		handlers.add(handler);
	}
 	
	public void removeEventHandler(BeeEventHandler handler) {
		handlers.remove(handler);
	}
	
	public void launch() throws BeeException {
		try {
			reset().makeAllTrips(new History());
		} catch (Exception e) {
			throw new BeeException(e);
		}
	}
	
	protected void makeAllTrips(History history) throws Exception {
		try (Hive hive = openHive()) {
			try (WebDownloader downloader = createWebDownloader()) {
				int tripNo = 1;
				for (Trip trip : this.trips) {
					trip.setTripNo(tripNo++);
					new BeeAsTripper(trip, downloader, hive, history).makeTrip();
				}
			}
			rewriteLinks(history);
		}
	}

	protected void rewriteLinks(History history) {
		Linker linker = this.hive.createLinker(history.getRedirections());
		for (Found f : history.getLinkSources()) {
			try {
				report(x->x.handleLinkStarted(f));
				linker.link(f.getLocalPath(), f.getMetadata());
				report(x->x.handleLinkCompleted(f));
			} catch (WebContentException | IOException e) {
				report(x->x.handleLinkFailed(f, e));
			} 
		}
	}
	
	protected Hive openHive() throws IOException {
		if (this.hive == null) {
			this.hive = new DefaultHive();
		}
		this.hive.open();
		return this.hive;
	}

	protected WebDownloader createWebDownloader() {
		return new BasicWebDownloader();
	}
	
	protected void addDefaultEventHandlers() {
		addEventHandler(new ConsoleLogger());
	}
	
	protected Bee reset() {
		this.nextResourceNo = 1;
		return this;
	}
	
	protected void report(Consumer<BeeEventHandler> action) {
		this.handlers.stream().forEach(action);
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
		protected Found newFound(WebResource resource) {
			int resourceNo = nextResourceNo++;
			return new Found(resourceNo, resource.getMetadata());
		}

		@Override
		protected void report(Consumer<BeeEventHandler> action) {
			Bee.this.report(action);
		}
		
		@Override
		protected void addEventHandler(BeeEventHandler handler) {
			Bee.this.addEventHandler(handler);
		}

		@Override
		protected void removeEventHandler(BeeEventHandler handler) {
			Bee.this.removeEventHandler(handler);
		}
	}
}
