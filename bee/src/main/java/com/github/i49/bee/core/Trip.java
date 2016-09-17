package com.github.i49.bee.core;

public class Trip {

	private final String startingPoint;
	private final int distanceLimit;

	public Trip(String startingPoint, int distanceLimit) {
		this.startingPoint = startingPoint.trim();
		this.distanceLimit = distanceLimit;
	}
	
	public String getStartingPoint() {
		return startingPoint;
	}
	
	public int getDistanceLimit() {
		return distanceLimit;
	}
}
