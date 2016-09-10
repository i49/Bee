package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebResource;

/**
 * Task to be done by bee.
 */
public class Task {

	private final Locator location;
	private final int distance;
	private final int level;
	private TaskPhase phase;
	private WebResource resource; 
	
	public Task(Locator location, int distance) {
		this(location, distance, 0);
		this.phase = TaskPhase.INITIAL;
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
	
	public TaskPhase getPhase() {
		return phase;
	}

	public void setPhase(TaskPhase phase) {
		this.phase = phase;
	}

	public Task createSubtask(Locator location) {
		return new Task(location, this.distance + 1, this.level + 1);
	}
}
