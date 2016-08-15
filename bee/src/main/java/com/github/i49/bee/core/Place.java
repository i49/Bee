package com.github.i49.bee.core;

import java.net.URL;

public class Place {

	private final URL location;
	private final int distance;
	
	public Place(URL location, int distance) {
		this.location = location;
		this.distance = distance;
	}

	public URL getLocation() {
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
