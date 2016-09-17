package com.github.i49.bee.core;

import java.util.HashSet;
import java.util.Set;

public class Trip {

	private final String startingPoint;
	private final int distanceLimit;

	private final Set<ResourceRecord> done = new HashSet<>();
	
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
	
	public boolean hasDone(ResourceRecord record) {
		return this.done.contains(record);
	}
	
	public void addDone(ResourceRecord record) {
		if (record != null) {
			this.done.add(record);
		}
	}
}
