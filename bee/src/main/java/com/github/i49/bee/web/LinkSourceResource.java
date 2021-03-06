package com.github.i49.bee.web;

import java.util.Collection;
import java.util.Map;

public interface LinkSourceResource extends WebResource {
	
	Collection<Link> getLinks();
	
	void rewriteLinks(Map<Locator, Locator> map);
}
