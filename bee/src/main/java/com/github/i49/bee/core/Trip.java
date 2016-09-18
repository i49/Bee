package com.github.i49.bee.core;

import java.util.function.Predicate;

import com.github.i49.bee.web.Link;

/**
 * A trip starting from specified location.
 * Each trip is imposed restriction on the distance from the starting point.
 */
public class Trip {

	private static final Predicate<Link> defaultExternalResourceLinkPredicate = new DefaultExternalResourceLinkPredicate();
	private static final Predicate<Link> defaultHyperlinkPredicate = new DefaultHyperlinkPredicate();
	
	private final String startingPoint;
	private final int distanceLimit;

	private Predicate<Link> externalResourceLinkPredicate;
	private Predicate<Link> hyperlinkPredicate;
	
	/**
	 * Constructs this trip.
	 * @param startingPoint starting point of this trip.
	 * @param distanceLimit distance limit from starting point.
	 */
	public Trip(String startingPoint, int distanceLimit) {
		this.startingPoint = startingPoint.trim();
		this.distanceLimit = distanceLimit;
		this.externalResourceLinkPredicate = defaultExternalResourceLinkPredicate;
		this.hyperlinkPredicate = defaultHyperlinkPredicate;
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
	
	public Predicate<Link> getExternalResourceLinkPredicate() {
		return externalResourceLinkPredicate;
	}
	
	public Predicate<Link> getHyperlinkPredicate() {
		return hyperlinkPredicate;
	}
}
