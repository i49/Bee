package com.github.i49.bee.web;

import java.nio.file.Path;

public class CacheReadException extends WebException {

	private static final long serialVersionUID = 1L;
	private final Locator location;
	private final Path cachePath;
	
	public CacheReadException(Locator location, Path cachePath, Throwable cause) {
		super(cause);
		this.location = location;
		this.cachePath = cachePath;
	}

	public Locator getLocation() {
		return location;
	}

	public Path getCachePath() {
		return cachePath;
	}

	@Override
	public String getMessage() {
		return "Failed to read cached resource at " + getLocation().toString() + " from "+ getCachePath().toString();
	}
}
