package com.github.i49.bee.web;

import java.io.InputStream;
import java.net.URI;

public class BinaryWebResource implements WebResource {

	private final URI location;
	private final MediaType mediaType;
	
	@Override
	public URI getLocation() {
		return location;
	}

	@Override
	public MediaType getMediaType() {
		return mediaType;
	}

	protected BinaryWebResource(URI location, MediaType mediaType) {
		this.location = location;
		this.mediaType = mediaType;
	}
	
	public static BinaryWebResource contentOf(URI location, MediaType mediaType, InputStream stream) {
		return new BinaryWebResource(location, mediaType);
	}
}
