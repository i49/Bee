package com.github.i49.bee.web;

import java.net.URI;

public abstract class AbstractWebResource implements WebResource {
	
	private final URI initialLocation;
	private final URI finalLocation;
	private final MediaType mediaType;

	protected AbstractWebResource(URI initialLocation, URI finalLocation, MediaType mediaType) {
		this.initialLocation = initialLocation;
		this.finalLocation = finalLocation;
		this.mediaType = mediaType;
	}
	
	@Override
	public URI getInitialLocation() {
		return initialLocation;
	}

	@Override
	public URI getFinalLocation() {
		return finalLocation;
	}

	@Override
	public MediaType getMediaType() {
		return mediaType;
	}
}
