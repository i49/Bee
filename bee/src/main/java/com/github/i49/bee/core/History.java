package com.github.i49.bee.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

public class History {

	private final List<Visit> visits = new ArrayList<>();
	private final Map<Locator, Visit> visitMap = new HashMap<>();
	private final Map<Locator, Locator> redirections = new HashMap<>();
	
	private long nextFoundId = 1;
	
	public History() {
	}

	public Found createFound(Locator location, ResourceMetadata metadata) {
		long id = this.nextFoundId++;
		return new Found(id, metadata);
	}
	
	public void addVisit(Visit v) {
		this.visits.add(v);
		this.visitMap.put(v.getLocation(), v);
		if (v.isRedirected()) {
			this.visitMap.put(v.getInitialLocation(), v);
			this.redirections.put(v.getInitialLocation(), v.getLocation());
		}
	}
	
	public Map<Locator, Locator> getRedirections() {
		return redirections;
	}
}
