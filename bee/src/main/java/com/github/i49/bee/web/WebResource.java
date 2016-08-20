package com.github.i49.bee.web;

import java.net.URI;

public interface WebResource {

	URI getLocation();
	
	MediaType getMediaType();
}
