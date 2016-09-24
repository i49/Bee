package com.github.i49.bee.core;

/**
 * A trip starting from specified location.
 * Each trip is imposed restriction on the distance from the starting point.
 */
public class Trip {

	private final String startingPoint;
	private final int distanceLimit;

	private final LinkStrategy linkStrategy = LinkStrategy.createDefault();
	
	/**
	 * Constructs this trip.
	 * @param startingPoint starting point of this trip.
	 * @param distanceLimit distance limit from starting point.
	 */
	public Trip(String startingPoint, int distanceLimit) {
		this.startingPoint = startingPoint.trim();
		this.distanceLimit = distanceLimit;
	}

	/**
	 * Returns starting point of this trip.
	 * @return starting point
	 */
	public String getStartingPoint() {
		return startingPoint;
	}
	
	/**
	 * Returns distance limit from starting point.
	 * @return distance limit
	 */
	public int getDistanceLimit() {
		return distanceLimit;
	}

	public LinkStrategy getLinkStrategy() {
		return linkStrategy;
	}
}
