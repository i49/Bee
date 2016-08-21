package com.github.i49.bee.web;

import java.net.URI;

public interface WebResource {

	URI getInitialLocation();
	
	URI getFinalLocation();
	
	MediaType getMediaType();
}
