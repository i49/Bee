package com.github.i49.bee.web;

import java.io.InputStream;
import java.net.URL;

public class BinaryWebResource implements WebResource {

	private final URL location;
	private final MediaType mediaType;
	
	@Override
	public URL getLocation() {
		return location;
	}

	@Override
	public MediaType getMediaType() {
		return mediaType;
	}

	protected BinaryWebResource(URL location, MediaType mediaType) {
		this.location = location;
		this.mediaType = mediaType;
	}
	
	public static BinaryWebResource contentOf(URL location, MediaType mediaType, InputStream stream) {
		return new BinaryWebResource(location, mediaType);
	}
}
