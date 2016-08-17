package com.github.i49.bee.core;

import java.net.URL;

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
	
	private final URL location;
	private final int distance;
	private Status status;
	
	public Task(URL location) {
		this(location, 0);
	}
	
	public Task(URL location, int distance) {
		this.location = location;
		this.distance = distance;
		this.status = Status.WAITING;
	}

	public URL getLocation() {
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
