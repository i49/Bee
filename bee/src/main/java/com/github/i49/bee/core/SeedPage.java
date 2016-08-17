package com.github.i49.bee.core;

import java.net.MalformedURLException;
import java.net.URL;

public class SeedPage {

	private final String location;
	private final int distanceLimit;

	public SeedPage(String location, int distanceLimit) {
		this.location = location.trim();
		this.distanceLimit = distanceLimit;
	}
	
	public String getLocation() {
		return location;
	}
	
	public int getDistanceLimit() {
		return distanceLimit;
	}
	
	public Task createPlace() throws MalformedURLException {
		return new Task(new URL(getLocation()), 0);
	}
}
