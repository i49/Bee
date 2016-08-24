package com.github.i49.bee.web;

import java.net.URI;

public interface WebResource {

	URI getLocation();
	
	URI getFinalLocation();
	
	void setRedirectLocation(URI location);
	
	MediaType getMediaType();
	
	byte[] getContent(ResourceSerializer serializer);
}
