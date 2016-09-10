package com.github.i49.bee.web;

/**
 * Exception thrown if a resource could not be retrieved.
 */
public class TransferException extends WebException {

	private static final long serialVersionUID = 1L;
	private final Locator location;
	
	public TransferException(Locator location, Throwable cause) {
		super(cause);
		this.location = location;
	}
	
	public Locator getLocation() {
		return location;
	}
	
	@Override
	public String getMessage() {
		return "Failed to transfer resource from: " + getLocation().toString();
	}
}
