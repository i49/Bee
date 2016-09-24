package com.github.i49.bee.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.i49.bee.web.Locator;

public class History {

	private final List<Visit> visits = new ArrayList<>();
	private final Map<Locator, Visit> visitMap = new HashMap<>();
	private final Map<Locator, Locator> redirections = new HashMap<>();
	private final Set<Found> linkSources = new LinkedHashSet<>();
	
	public History() {
	}

	public void addVisit(Visit v) {
		this.visits.add(v);
		addFirstVisitTo(v.getLocation(), v);
		if (v.isRedirected()) {
			addFirstVisitTo(v.getInitialLocation(), v);
			this.redirections.put(v.getInitialLocation(), v.getLocation());
		}
		Found found = v.getFound();
		if (found != null && found.isLinkSource()) {
			linkSources.add(found);
		}
	}
	
	public Visit recallVisit(Locator location) {
		if (location == null) {
			return null;
		}
		return this.visitMap.get(location);
	}
	
	public Collection<Found> getLinkSources() {
		return linkSources;
	}
	
	public Map<Locator, Locator> getRedirections() {
		return redirections;
	}
	
	private void addFirstVisitTo(Locator location, Visit v) {
		if (this.visitMap.get(location) == null) {
			this.visitMap.put(location, v);
		}
	}
}
