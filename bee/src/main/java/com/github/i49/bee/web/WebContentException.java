package com.github.i49.bee.web;

public class WebContentException extends WebException {

	private static final long serialVersionUID = 1L;
	private final Locator location;
	
	public WebContentException(Locator location, Throwable cause) {
		super(cause);
		this.location = location;
	}
	
	public Locator getLocation() {
		return location;
	}

	@Override
	public String getMessage() {
		return "Invalid resource content from: " + getLocation();
	}
}
