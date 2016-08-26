package com.github.i49.bee.web;

public abstract class AbstractWebResource implements WebResource {
	
	private final Locator location;
	private Locator redirectLocation;
	private final MediaType mediaType;

	protected AbstractWebResource(Locator location, MediaType mediaType) {
		this.location = location;
		this.mediaType = mediaType;
	}
	
	@Override
	public Locator getLocation() {
		return location;
	}

	@Override
	public Locator getFinalLocation() {
		if (this.redirectLocation != null) {
			return this.redirectLocation;
		} else {
			return location;
		}
	}

	@Override
	public void setRedirectLocation(Locator location) {
		this.redirectLocation = location;
	}
	
	@Override
	public MediaType getMediaType() {
		return mediaType;
	}
}
