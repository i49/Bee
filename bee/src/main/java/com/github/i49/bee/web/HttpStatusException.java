package com.github.i49.bee.web;

public class HttpStatusException extends WebException {

	private static final long serialVersionUID = 1L;
	private final Locator location;
	private final int statusCode;
	
	public HttpStatusException(Locator location, int statusCode) {
		this.location = location;
		this.statusCode = statusCode;
	}

	public Locator getLocation() {
		return location;
	}

	public int getStatusCode() {
		return statusCode;
	}

	@Override
	public String getMessage() {
		return "HTTP status code is " + getStatusCode() + " for " + getLocation().toString();
	}
}
