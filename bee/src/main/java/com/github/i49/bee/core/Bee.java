package com.github.i49.bee.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.WebResource;

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
		for (SeedPage page: this.seeds) {
			try {
				URL location = new URL(page.getLocation());
				travel(location, page.getDistance());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void travel(URL locationToStart, int moreToGo) {
		
	}
	
	protected void visit(URL location, int moreToGo) {
		WebResource resource = fetch(location);
	}
	
	protected WebResource fetch(URL location) {
		log.debug("Downloading " + location.toString());
		return null;
	}
	
	protected boolean canVisit(URL location) {
		for (WebSite site: this.sites) {
			if (site.contains(location)) {
				return true;
			}
		}
		return false;
	}
}
