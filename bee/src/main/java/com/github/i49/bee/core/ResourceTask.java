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
	private ResourceRecord record;
	private Exception errorCause;
	
	private final Map<Locator, ResourceMetadata> links = new LinkedHashMap<>();
	
	public ResourceTask(Locator location, int distance) {
		this(location, distance, 0);
		this.phase = ResourceTaskPhase.INITIAL;
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

	public void setResource(WebResource resource) {
		this.resource = resource;
	}
	
	public ResourceRecord getRecord() {
		return record;
	}
	
	public int getResourceId() {
		return (record != null) ? record.getId() : -1;
	}
	
	public ResourceMetadata getMetadata() {
		return (record != null) ? record.getMetadata() : null;
	}
	
	public void setRecord(ResourceRecord record) {
		this.record = record;
	}

	public ResourceTaskPhase getPhase() {
		return phase;
	}

	public void setPhase(ResourceTaskPhase phase) {
		this.phase = phase;
	}

	public Exception getErrorCause() {
		return errorCause;
	}
	
	public void setErrorCause(Exception cause) {
		this.errorCause = cause;
	}
	
	public ResourceTask createSubtask(Locator location) {
		return new ResourceTask(location, this.distance + 1, this.level + 1);
	}

	@Override
	protected boolean doBeforeSubtasks() {
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
			setResource(resource);
			setRecord(recordResource(location, resource));
			getVisitor().notifyEvent(x->x.handleTaskEvent(this));
		} catch (WebException e) {
			setErrorCause(e);
			getVisitor().notifyEvent(x->x.handleTaskFailure(this));
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
		if (this.record.isStored()) {
			return;
		}
		try {
			getVisitor().getHive().store(getResource(), this.links);
			getRecord().setStored();
			getVisitor().notifyEvent(x->x.handleTaskEvent(this));
		} catch (IOException e) {
		}
	}
	
	protected ResourceRecord recordResource(Locator location, WebResource resource) {
		return getVisitor().getRegistry().register(location, resource.getMetadata());
	}
}
