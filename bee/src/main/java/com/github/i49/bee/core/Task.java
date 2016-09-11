package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebResource;

/**
 * Task to collect web resource.
 */
public class Task {

	private final Locator location;
	private final int distance;
	private final int level;
	private ResourceTaskPhase phase;
	private WebResource resource; 
	private ResourceRecord record;
	private Exception cause;
	
	public Task(Locator location, int distance) {
		this(location, distance, 0);
		this.phase = ResourceTaskPhase.INITIAL;
	}

	public Task(Locator location, int distance, int level) {
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
		ResourceRecord record = getRecord();
		return (record != null) ? record.getId() : -1;
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

	public Exception getCause() {
		return cause;
	}
	
	public void setCause(Exception cause) {
		this.cause = cause;
	}
	
	public Task createSubtask(Locator location) {
		return new Task(location, this.distance + 1, this.level + 1);
	}
}
