package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

/**
 * Task to be done by bee.
 */
public class Task {

	private final Locator location;
	private final int distance;
	
	public Task(Locator location) {
		this(location, 0);
	}
	
	public Task(Locator location, int distance) {
		this.location = location;
		this.distance = distance;
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
}
