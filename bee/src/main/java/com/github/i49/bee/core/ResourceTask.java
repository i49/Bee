package com.github.i49.bee.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.github.i49.bee.hives.HiveException;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.WebResource;

/**
 * Task to collect web resource.
 */
public class ResourceTask extends Task {

	private final Locator location;
	private final int distance;
	private final int level;

	private WebResource resource; 
	
	private final Map<Locator, ResourceMetadata> links = new LinkedHashMap<>();
	private final List<Task> futureTasks = new ArrayList<>();
	
	public ResourceTask(Locator location, int distance) {
		this(location, distance, 0);
	}

	public ResourceTask(Locator location, int distance, int level) {
		this.location = location;
		this.distance = distance;
		this.level = level;
	}

	public Locator getLocation() {
		return location;
	}

	/**
	 * Returns distance from starting place.
	 * @return distance from starting place
	 */
	public int getDistance() {
		return distance;
	}
	
	public int getLevel() {
		return level;
	}
	
	public WebResource getResource() {
		return resource;
	}

	public Visit getVisit() {
		VisitMap map = getVisitor().getVisitMap();
		return map.findVisit(getLocation());
	}
	
	public int getResourceId() {
		Visit visit = getVisit();
		return (visit != null) ? visit.getId() : -1;
	}
	
	public ResourceMetadata getMetadata() {
		Visit visit = getVisit();
		return (visit != null) ? visit.getMetadata() : null;
	}
	
	public List<Task> getFutureTasks() {
		return futureTasks;
	}
	
	@Override
	protected boolean runBeforeSubtasks() {
		if (getVisitor().hasDone(getLocation())) {
			return false;
		}
		try {
			retrieveResource();
			parseResource();
			return true;
		} catch (WebException e) {
			return false;
		}
	}

	@Override
	protected void runAfterSubtasks() {
		storeResource();
	}

	@Override
	protected void runAfterEachSubtask(Task subtask) {
		ResourceTask actual = (ResourceTask)subtask;
		this.links.put(actual.getLocation(), actual.getMetadata());
		if (subtask instanceof HyperlinkResourceTask) {
			HyperlinkResourceTask resourceTask = (HyperlinkResourceTask)subtask;
			Visit record = resourceTask.getVisit();
			if (record != null) {
				visitLater(record.getMetadata());
			}
		}
	}

	protected void retrieveResource() throws WebException {
		try {
			notifyEvent(x->x.handleDownloadStarted(getDistance(), getLevel(), getLocation()));
			WebResource resource =  getVisitor().getDownloader().download(getLocation());
			recordVisit(getLocation(), resource);
			notifyEvent(x->x.handleDownloadCompleted(getDistance(), getLevel(), getVisit()));
		} catch (WebException e) {
			notifyEvent(x->x.handleDownloadFailed(getDistance(), getLevel(), getLocation(), e));
			throw e;
		}
	}

	protected void parseResource() {
		if (!(this.resource instanceof LinkSourceResource)) {
			return;
		}
		LinkSourceResource resource = (LinkSourceResource)this.resource;
		Collection<Link> links = resource.getLinks();
		for (Link link : filterExternalResourceLinks(links)) {
			Locator location = link.getLocation();
			if (getVisitor().canVisit(location)) {
				addSubtask(new ExternalResourceTask(location, getDistance() + 1, getLevel() + 1));
			}
		}
		for (Link link : filterHyperlinks(links)) {
			final Locator location = link.getLocation();
			final int distance = getDistance() + 1;
			if (getVisitor().canVisit(location, distance)) {
				addSubtask(new HyperlinkResourceTask(location, distance, getLevel() + 1));
			}
		}
	}
	
	protected List<Link> filterExternalResourceLinks(Collection<Link> links) {
		return links.stream().filter(getVisitor().getExternalResourceLinkPredicate()).collect(Collectors.toList());
	}
	
	protected List<Link> filterHyperlinks(Collection<Link> links) {
		return links.stream().filter(getVisitor().getHyperlinkPredicate()).collect(Collectors.toList());
	}

	protected void storeResource() {
		Visit visit = getVisit();
		if (visit.isStored()) {
			return;
		}
		try {
			notifyEvent(x->x.handleStoreStarted(getDistance(), getLevel(), getVisit()));
			getVisitor().getHive().store(getResource());
			visit.setStored();
			getVisitor().addDone(visit);
			notifyEvent(x->x.handleStoreCompleted(getDistance(), getLevel(), getVisit()));
		} catch (IOException e) {
			notifyEvent(x->x.handleStoreFailed(getDistance(), getLevel(), getVisit(), e));
		}
	}
	
	protected Visit recordVisit(Locator location, WebResource resource) {
		this.resource = resource;
		VisitMap map = getVisitor().getVisitMap();
		return map.addVisit(location, resource.getMetadata());
	}
	
	protected void visitLater(ResourceMetadata metadata) {
		Locator location = metadata.getLocation();
		int distance = getDistance() + 1;
		if (getVisitor().canVisit(location, distance)) {
			this.futureTasks.add(new ResourceTask(location, distance));
		}
	}
}
