package com.github.i49.bee.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Bee {
	
	private static final Log log = LogFactory.getLog(Bee.class);
	
	private final List<SeedPage> seeds = new ArrayList<>();
	private final List<WebSite> sites = new ArrayList<>();
	
	public Bee() {
	}

	public List<SeedPage> getSeeds() {
		return seeds;
	}
	
	public List<WebSite> getSites() {
		return sites;
	}
	
	public void buzz() {
		log.debug("Starting buzz()");
	}
}
