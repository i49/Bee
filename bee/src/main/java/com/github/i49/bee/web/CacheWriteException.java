package com.github.i49.bee.web;

public class CacheWriteException extends WebException {

	private static final long serialVersionUID = 1L;
	private final Locator location;
	
	public CacheWriteException(Locator location, Throwable cause) {
		super(cause);
		this.location = location;
	}

	public Locator getLocation() {
		return location;
	}

	@Override
	public String getMessage() {
		return "Failed to cache resource of " + getLocation().toString();
	}
}
