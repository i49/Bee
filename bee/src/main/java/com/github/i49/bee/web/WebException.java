package com.github.i49.bee.web;

/**
 * Root of the hierarchy of web resource exceptions.
 */
public abstract class WebException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebException() {
		super();
	}
	
	public WebException(Throwable cause) {
		super(cause);
	}
}
