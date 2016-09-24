package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class Visit {

	private final int tripNo;
	private final int visitNo;
	private final Locator initialLocation;
	private final int distance;
	private Found found;
	
	public Visit(int tripNo, int visitNo, Locator location, int distance) {
		this.tripNo = tripNo;
		this.visitNo = visitNo;
		this.initialLocation = location;
		this.distance = distance;
	}
	
	public int getTripNo() {
		return tripNo;
	}
	
	public int getVisitNo() {
		return visitNo;
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
	
	public void setFoundOf(Visit other) {
		this.found = other.found;
	}
}
