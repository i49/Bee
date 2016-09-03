package com.github.i49.bee.web;

import java.util.Collection;
import java.util.Map;

public interface LinkProvidingResource extends WebResource {
	
	Collection<Link> getDependencyLinks();
	
	Collection<Link> getExternalLinks();
	
	void rewriteLinks(Map<Locator, Locator> map);
}
