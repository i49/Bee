package com.github.i49.bee.core;

public class Seed {

	private final String location;
	private final int distanceLimit;

	public Seed(String location, int distanceLimit) {
		this.location = location.trim();
		this.distanceLimit = distanceLimit;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getDistanceLimit() {
		return distanceLimit;
	}
}
