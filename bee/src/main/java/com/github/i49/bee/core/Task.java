package com.github.i49.bee.core;

import java.net.URI;

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
	
	private final URI location;
	private final int distance;
	private Status status;
	
	public Task(URI location) {
		this(location, 0);
	}
	
	public Task(URI location, int distance) {
		this.location = location;
		this.distance = distance;
		this.status = Status.WAITING;
	}

	public URI getLocation() {
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
