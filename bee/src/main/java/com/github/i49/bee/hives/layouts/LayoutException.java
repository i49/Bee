package com.github.i49.bee.hives.layouts;

public class LayoutException extends Exception {

	private static final long serialVersionUID = 1L;
	private final String path;
	
	public LayoutException(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
