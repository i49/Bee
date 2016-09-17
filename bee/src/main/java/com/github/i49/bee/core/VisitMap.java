package com.github.i49.bee.core;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

public class VisitMap {

	private final Map<Locator, Visit> visits = new HashMap<>();
	private int nextId = 1;
			
	public VisitMap() {
	}
	
	public Visit addVisit(Locator location, ResourceMetadata metadata) {
		if (location == null || metadata == null) {
			throw new IllegalArgumentException();
		}
		Visit entry = findVisit(location);
		if (entry != null) {
			return entry;
		}
		entry = findVisit(metadata.getLocation());
		if (entry == null) {
			entry = new Visit(this.nextId++, metadata);
			visits.put(metadata.getLocation(), entry);
		}
		if (!location.equals(metadata.getLastModified())) {
			visits.put(location, entry);
		}
		return entry;
	}

	public Visit findVisit(Locator location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		return visits.get(location);
	}
}
