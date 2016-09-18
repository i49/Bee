package com.github.i49.bee.hives;

public class HiveException extends Exception {

	private static final long serialVersionUID = 1L;

	public HiveException() {
	}

	public HiveException(Throwable cause) {
		super(cause);
	}

	@Override
	public String getMessage() {
		return getCause().getMessage();
	}
}
