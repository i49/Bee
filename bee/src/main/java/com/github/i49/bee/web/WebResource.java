package com.github.i49.bee.web;

public interface WebResource {

	ResourceMetadata getMetadata();
	
	byte[] getContent(ResourceSerializer serializer);
}
