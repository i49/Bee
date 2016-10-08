package com.github.i49.bee.hives.layouts;

public class NotCellException extends LayoutException {

	private static final long serialVersionUID = 1L;

	public NotCellException(String path) {
		super(path);
	}
	
	@Override
	public String getMessage() {
		return "Entry already exists for path: " + getPath();
	}
}
