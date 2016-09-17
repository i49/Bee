package com.github.i49.bee.core;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkProvidingResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebException;
import com.github.i49.bee.web.WebResource;

/**
 * Task to collect web resource.
 */
public class ResourceTask extends VisitorTask {

	private final Locator location;
	private final int distance;
	private final int level;

	private ResourceTaskPhase phase;
	private WebResource resource; 
	
	private final Map<Locator, ResourceMetadata> links = new LinkedHashMap<>();
	
	public ResourceTask(Locator location, int distance) {
		this(location, distance, 0);
		setPhase(ResourceTaskPhase.INITIAL);
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

	public ResourceRecord getRecord() {
		ResourceRegistry registry = getVisitor().getRegistry();
		return registry.find(getLocation());
	}
	
	public int getResourceId() {
		ResourceRecord record = getRecord();
		return (record != null) ? record.getId() : -1;
	}
	
	public ResourceMetadata getMetadata() {
		ResourceRecord record = getRecord();
		return (record != null) ? record.getMetadata() : null;
	}
	
	public ResourceTaskPhase getPhase() {
		return phase;
	}

	public void setPhase(ResourceTaskPhase phase) {
		this.phase = phase;
	}

	@Override
	protected boolean doBeforeSubtasks() {
		if (getVisitor().hasDone(getLocation())) {
			return false;
		}
		setPhase(ResourceTaskPhase.GET);
		try {
			retrieveResource();
			parseResource();
			return true;
		} catch (WebException e) {
			return false;
		}
	}

	@Override
	protected void doAfterSubtasks() {
		setPhase(ResourceTaskPhase.STORE);
		storeResource();
	}

	@Override
	protected void doAfterSubtask(Task<Visitor> subtask) {
		ResourceTask actual = (ResourceTask)subtask;
		this.links.put(actual.getLocation(), actual.getMetadata());
	}

	protected void retrieveResource() throws WebException {
		try {
			Locator location = getLocation();
			WebResource resource =  getVisitor().getDownloader().download(location);
			this.resource = resource;
			recordResource(location, resource);
			getVisitor().notifyEvent(x->x.handleTaskEvent(this));
		} catch (WebException e) {
			getVisitor().notifyEvent(x->x.handleTaskFailure(this, e));
			throw e;
		}
	}

	protected void parseResource() {
		if (!(this.resource instanceof LinkProvidingResource)) {
			return;
		}
		LinkProvidingResource resource = (LinkProvidingResource)this.resource;
		for (Link link : resource.getDependencyLinks()) {
			Locator location = link.getLocation();
			if (getVisitor().canVisit(location)) {
				addSubtask(new ExternalResourceTask(location, getDistance() + 1, getLevel() + 1));
			}
		}
		for (Link link : resource.getExternalLinks()) {
			final Locator location = link.getLocation();
			final int distance = getDistance() + 1;
			if (getVisitor().canVisit(location, distance)) {
				addSubtask(new HyperlinkResourceTask(location, distance, getLevel() + 1));
			}
		}
	}
	
	protected void storeResource() {
		ResourceRecord record = getRecord();
		if (record.isStored()) {
			return;
		}
		try {
			getVisitor().getHive().store(getResource(), this.links);
			record.setStored();
			getVisitor().addDone(record);
			getVisitor().notifyEvent(x->x.handleTaskEvent(this));
		} catch (IOException e) {
		}
	}
	
	protected ResourceRecord recordResource(Locator location, WebResource resource) {
		ResourceRegistry registry = getVisitor().getRegistry();
		return registry.register(location, resource.getMetadata());
	}
}
