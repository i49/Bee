package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.AccessMode;

public class StorageIOException extends IOException {

	private static final long serialVersionUID = 1L;

	private final String localPath;
	private final AccessMode mode;
	
	public StorageIOException(String localPath, AccessMode mode, IOException cause) {
		super(cause);
		this.localPath = localPath;
		this.mode = mode;
	}
	
	public String getLocalPath() {
		return localPath;
	}
	
	@Override
	public IOException getCause() {
		return (IOException)super.getCause();
	}
	
	@Override
	public String getMessage() {
		if (this.mode == AccessMode.READ) {
			return "Failed to read content from storage at " + getLocalPath();
		} else if (this.mode == AccessMode.WRITE) {
			return "Failed to write content into storage at " + getLocalPath();
		} else {
			return null;
		}
	}
}
