package com.github.i49.bee.core;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.WebResource;

public abstract class Tripper {

	private final int tripNo;
	private final Trip trip;
	private final WebDownloader downloader;
	private final Hive hive;
	private final History history;
	
	private int nextVisitNo;
	private final LinkedList<Visit> visits = new LinkedList<>();
	
	public Tripper(int tripNo, Trip trip, WebDownloader downloader, Hive hive, History history) {
		this.tripNo = tripNo;
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
		visitAll();
	}
	
	private void visitAll() {
		while (!visits.isEmpty()) {
			visit(visits.removeFirst());
		}
	}
	
	private void visit(Visit v) {
		try {
			tryVisit(v);
			addNextVisits(v);
		} catch (WebException e) {
		} catch (IOException e) {
		} finally {
			history.addVisit(v);
		}
	}

	private void tryVisit(Visit v) throws WebException, IOException {
		WebResource resource = retrieveResource(v);
		if (resource instanceof LinkSourceResource) {
			parseResource(v, (LinkSourceResource)resource);
		}
		storeResource(v, resource);
	}
	
	private WebResource retrieveResource(Visit v) throws WebException {
		report(x->x.handleDownloadStarted(v));
		WebResource resource = this.downloader.download(v.getLocation());
		Found found = newFound(resource);
		v.setFound(found);
		report(x->x.handleDownloadCompleted(v));
		return resource;
	}
	
	private void parseResource(Visit v, LinkSourceResource resource) {
		Collection<Link> links = resource.getLinks();
		Found found = v.getFound();
		found.setHyperlinks(filterLinks(links, getHyperlinkSelector()));
		found.setExternalResourceLinks(filterLinks(links, getExternalResourceLinkSelector()));
	}
	
	private void storeResource(Visit v, WebResource resource) throws IOException {
		report(x->x.handleStoreStarted(v));
		String localPath = this.hive.store(resource);
		v.getFound().setLocalPath(localPath);
		report(x->x.handleStoreCompleted(v));
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
		return new Visit(this.tripNo, visitNo, location, distance);
	}
	
	private void addNextVisits(Visit v) {
		if (!v.hasFound()) {
			return;
		}
		Found found = v.getFound();
		int nextDistance = v.getDistance() + 1;
		int i = 0;
		for (Locator location : found.getExternalResourceLinks()) {
			this.visits.add(i++, newVisit(location, nextDistance));
		}
		if (nextDistance < this.trip.getDistanceLimit()) {
			for (Locator location : found.getHyperlinks()) {
				this.visits.add(i++, newVisit(location, nextDistance));
			}
		}
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
}
