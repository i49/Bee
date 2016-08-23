package com.github.i49.bee.web;

import java.net.URI;

public abstract class AbstractWebResource implements WebResource {
	
	private final URI location;
	private URI redirectLocation;
	private final MediaType mediaType;

	protected AbstractWebResource(URI location, MediaType mediaType) {
		this.location = location;
		this.mediaType = mediaType;
	}
	
	@Override
	public URI getLocation() {
		return location;
	}

	@Override
	public URI getFinalLocation() {
		if (this.redirectLocation != null) {
			return this.redirectLocation;
		} else {
			return location;
		}
	}

	@Override
	public void setRedirectLocation(URI location) {
		this.redirectLocation = location;
	}
	
	@Override
	public MediaType getMediaType() {
		return mediaType;
	}
}
