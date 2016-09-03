package com.github.i49.bee.web;

public abstract class WebResource {

	private final ResourceMetadata metadata;
	
	public WebResource(ResourceMetadata metadata) {
		this.metadata = metadata;
	}

	public ResourceMetadata getMetadata() {
		return metadata;
	}

	public abstract byte[] getBytes(ResourceSerializer serializer);
}
