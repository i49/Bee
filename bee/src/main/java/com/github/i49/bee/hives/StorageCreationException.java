package com.github.i49.bee.hives;

import java.nio.file.Path;

public class StorageCreationException extends HiveException {

	private static final long serialVersionUID = 1L;

	private final Path path;
	
	public StorageCreationException(Path path, Throwable cause) {
		super(cause);
		this.path = path;
	}
	
	public Path getPath() {
		return path;
	}
	
	@Override
	public String getMessage() {
		return "Failed to create storage at " + getPath().toString();
	}
}
