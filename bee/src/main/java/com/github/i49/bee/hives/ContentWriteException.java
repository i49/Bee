package com.github.i49.bee.hives;

import java.nio.file.Path;

public class ContentWriteException extends HiveException {

	private static final long serialVersionUID = 1L;

	private final Path path;
	
	public ContentWriteException(Path path, Throwable cause) {
		super(cause);
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
	
	@Override
	public String getMessage() {
		return "Failed to write content to " + getPath().toString();
	}
}
