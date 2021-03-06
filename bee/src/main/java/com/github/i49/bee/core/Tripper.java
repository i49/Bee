package com.github.i49.bee.core;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.hives.HiveException;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.WebResource;

public abstract class Tripper {

	private final Trip trip;
	private final WebDownloader downloader;
	private final Hive hive;
	private final History history;
	
	private int nextVisitNo;
	private final LinkedList<Visit> visits = new LinkedList<>();
	private final StatisticsCollector statsCollector = new StatisticsCollector();
	
	public Tripper(Trip trip, WebDownloader downloader, Hive hive, History history) {
		this.trip = trip;
		this.downloader = downloader;
		this.hive = hive;
		this.history = history;
		this.nextVisitNo = 1;
	}

	public void makeTrip() {
		Locator location = Locator.parse(trip.getStartingPoint());
		if (location == null) {
			return;
		}

		visits.addFirst(newVisit(location, 0));

		report(x->x.handleTripStarted(trip));
		addEventHandler(statsCollector);
		visitAll();
		removeEventHandler(statsCollector);
		report(x->x.handleTripCompleted(trip, statsCollector));
	}
	
	private void visitAll() {
		while (!visits.isEmpty()) {
			visit(visits.removeFirst());
		}
	}
	
	private void visit(Visit v) {
		try {
			tryVisit(v);
			addVisitsAfter(v);
		} catch (WebException e) {
			report(x->x.handleDownloadFailed(v, e));
		} catch (HiveException e) {
			report(x->x.handleStoreFailed(v, e));
		} finally {
			history.addVisit(v);
		}
	}
	
	private void tryVisit(Visit v) throws WebException, HiveException {
		try {
			report(x->x.handleVisitStarted(v));
			tryFirstVisit(v);
			report(x->x.handleVisitCompleted(v));
		} catch (VisitedException e) {
			v.setFoundOf(e.getEarlierVisit());
			report(x->x.handleVisitSkipped(v, e));
		}
	}
	
	private void tryFirstVisit(Visit v) throws WebException, HiveException, VisitedException {
		assertFirstVisit(v.getLocation());
		WebResource resource = retrieveResource(v);
		if (resource instanceof LinkSourceResource) {
			parseResource(v, (LinkSourceResource)resource);
		}
		storeResource(v, resource);
	}
	
	private WebResource retrieveResource(Visit v) throws WebException, VisitedException {
		report(x->x.handleDownloadStarted(v));
		WebResource resource = this.downloader.download(v.getLocation());
		v.setDownloaded(resource.getMetadata());
		report(x->x.handleDownloadCompleted(v));
		assertFirstVisit(resource.getMetadata().getLocation());
		v.setFound(newFound(resource));
		return resource;
	}
	
	private void parseResource(Visit v, LinkSourceResource resource) {
		Found f = v.getFound().markAsLinkSource();
		Collection<Link> links = resource.getLinks();
		f.setHyperlinks(filterLinks(links, getHyperlinkSelector()));
		f.setExternalResourceLinks(filterLinks(links, getExternalResourceLinkSelector()));
	}
	
	private void storeResource(Visit v, WebResource resource) throws HiveException {
		report(x->x.handleStoreStarted(v));
		String localPath = this.hive.store(resource);
		v.getFound().setLocalPath(localPath);
		report(x->x.handleStoreCompleted(v));
	}

	private void assertFirstVisit(Locator location) throws VisitedException {
		Visit earlierVisit = history.recallVisit(location);
		if (earlierVisit != null) {
			throw new VisitedException(earlierVisit);
		}
	}
	
	private List<Locator> filterLinks(Collection<Link> links, Predicate<Link> selector) {
		return links.stream()
			.filter(selector)
			.map(Link::getLocation)
			.filter(location->canVisit(location))
			.distinct()
			.collect(Collectors.toList());
	}
	
	private Visit newVisit(Locator location, int distance) {
		int visitNo = this.nextVisitNo++;
		return new Visit(trip.getTripNo(), visitNo, location, distance);
	}
	
	private void addVisitsAfter(Visit v) {
		if (!v.hasFound()) {
			return;
		}
		Found found = v.getFound();
		int nextDistance = v.getDistance() + 1;
		int pos = addVisits(0, found.getExternalResourceLinks(), nextDistance);
		if (nextDistance <= this.trip.getDistanceLimit()) {
			addVisits(pos, found.getHyperlinks(), nextDistance);
		}
	}
	
	private int addVisits(int pos, List<Locator> locations, int distance) {
		for (Locator location : locations) {
			Visit visited = history.recallVisit(location);
			if (visited == null || !visited.hasFound() || visited.getFound().hasLinks()) {
				this.visits.add(pos++, newVisit(location, distance));
			}
		}
		return pos;
	}
	
	protected Predicate<Link> getHyperlinkSelector() {
		return trip.getLinkStrategy().getHyperlinkSelector();
	}
	
	protected Predicate<Link> getExternalResourceLinkSelector() {
		return trip.getLinkStrategy().getExternalResoureLinkSelector();
	}

	protected abstract boolean canVisit(Locator location);
	
	protected abstract Found newFound(WebResource resource);
	
	protected abstract void report(Consumer<BeeEventHandler> action);
	
	protected abstract void addEventHandler(BeeEventHandler handler);
	
	protected abstract void removeEventHandler(BeeEventHandler handler);
}
