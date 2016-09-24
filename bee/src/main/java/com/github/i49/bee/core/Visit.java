package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class Visit {

	private final Locator initialLocation;
	private final int distance;
	private Found found;
	
	public Visit(Locator location, int distance) {
		this.initialLocation = location;
		this.distance = distance;
	}

	public Locator getLocation() {
		if (hasFound()) {
			return getFound().getMetadata().getLocation();
		} else {
			return getInitialLocation();
		}
	}

	public Locator getInitialLocation() {
		return initialLocation;
	}
	
	public boolean isRedirected() {
		return !getLocation().equals(getInitialLocation());
	}
	
	public int getDistance() {
		return distance;
	}

	public boolean hasFound() {
		return getFound() != null;
	}

	public Found getFound() {
		return found;
	}
	
	public void setFound(Found found) {
		this.found = found;
	}
}
