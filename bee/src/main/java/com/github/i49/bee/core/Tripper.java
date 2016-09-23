package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.WebResource;

public abstract class Tripper {

	private final Trip trip;
	private final WebDownloader downloader;
	
	private final LinkedList<Visit> visits = new LinkedList<>();
	
	private List<Locator> externalResourceLinks;
	private List<Locator> hyperlinks;

	public Tripper(Trip trip, WebDownloader downloader) {
		this.trip = trip;
		this.downloader = downloader;
	}

	public void makeTrip() {
		Locator location = Locator.parse(trip.getStartingPoint());
		if (location == null) {
			return;
		}
		visits.addFirst(new Visit(location, 0));
		while (!visits.isEmpty()) {
			visit(visits.removeFirst());
		}
	}
	
	private void visit(Visit v) {
		try {
			tryVisit(v);
			addNextVisits(v.getDistance() + 1);
		} catch (WebException e) {
		} catch (IOException e) {
		} finally {
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
		WebResource resource = this.downloader.download(v.getLocation());
		return resource;
	}
	
	private void parseResource(Visit v, LinkSourceResource resource) {
		Collection<Link> links = resource.getLinks();
		this.externalResourceLinks = links.stream().map(x->x.getLocation()).collect(Collectors.toList());
	}
	
	private void storeResource(Visit v, WebResource resource) throws IOException {
		String localPath = getHive().store(resource);
	}

	private void addNextVisits(int nextDistance) {
		int i = 0;
		for (Locator location : this.externalResourceLinks) {
			this.visits.add(i++, new Visit(location, nextDistance));
		}
		if (nextDistance < this.trip.getDistanceLimit()) {
			for (Locator location : this.hyperlinks) {
				this.visits.add(i++, new Visit(location, nextDistance));
			}
		}
		this.externalResourceLinks = null;
		this.hyperlinks = null;
	}

	protected abstract boolean canVisit(Locator location);
	
	protected abstract Hive getHive();
	
	protected abstract void report(Consumer<BeeEventHandler> action);
}
