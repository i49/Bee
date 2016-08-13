package com.github.i49.bee.core;

public class SeedPage {

	private final String location;
	private final int distance;

	public SeedPage(String location, int distance) {
		this.location = location.trim();
		this.distance = distance;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getDistance() {
		return distance;
	}
}
