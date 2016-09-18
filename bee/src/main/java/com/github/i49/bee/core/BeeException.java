package com.github.i49.bee.core;

public class BeeException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeeException() {
	}

	public BeeException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return getCause().getMessage();
	}
}
