package com.github.i49.bee.web;

public abstract class AbstractWebResource implements WebResource {

	private final ResourceMetadata metadata;
	
	protected AbstractWebResource(ResourceMetadata metadata) {
		this.metadata = metadata;
	}

	public ResourceMetadata getMetadata() {
		return metadata;
	}
}
