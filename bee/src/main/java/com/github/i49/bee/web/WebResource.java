package com.github.i49.bee.web;

public abstract class WebResource {

	private final ResourceMetadata metadata;
	
	protected WebResource(ResourceMetadata metadata) {
		this.metadata = metadata;
	}
	
	public ResourceMetadata getMetadata() {
		return metadata;
	}
	
	public abstract byte[] getContent(ResourceSerializer serializer);
}
