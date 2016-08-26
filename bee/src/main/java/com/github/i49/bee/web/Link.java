package com.github.i49.bee.web;

public class Link {
	
	private final Locator location;
	private final MediaType mediaType;
	
	public Locator getLocation() {
		return location;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	protected Link(Locator location, MediaType mediaType) {
		this.location = location;
		this.mediaType = mediaType;
	}

	public static Link create(Locator location, MediaType mediaType) {
		if (location == null) {
			return null;
		}
		return new Link(location, mediaType);
	}
}
