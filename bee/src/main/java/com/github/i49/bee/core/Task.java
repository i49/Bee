package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

/**
 * Task to be done by bee.
 */
public class Task {

	public enum Status {
		WAITING,
		SKIPPED,
		FAILED,
		DONE
	};
	
	private final Locator location;
	private final int distance;
	private Status status;
	
	public Task(Locator location) {
		this(location, 0);
	}
	
	public Task(Locator location, int distance) {
		this.location = location;
		this.distance = distance;
		this.status = Status.WAITING;
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
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
}
