package com.github.i49.bee.buzz;

public class BuzzException extends Exception {

	private static final long serialVersionUID = 1L;

	public BuzzException() {
	}
	
	public BuzzException(Exception cause) {
		super(cause);
	}
	
	@Override
	public String getMessage() {
		return getCause().getMessage();
	}
}
