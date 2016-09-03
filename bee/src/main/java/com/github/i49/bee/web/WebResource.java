package com.github.i49.bee.web;

public interface WebResource {

	ResourceMetadata getMetadata();

	byte[] getBytes(ResourceSerializer serializer);
}
