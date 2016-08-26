package com.github.i49.bee.web;

public interface WebResource {

	Locator getLocation();
	
	Locator getFinalLocation();
	
	void setRedirectLocation(Locator location);
	
	MediaType getMediaType();
	
	byte[] getContent(ResourceSerializer serializer);
}
