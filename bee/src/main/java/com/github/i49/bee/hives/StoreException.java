package com.github.i49.bee.hives;

import com.github.i49.bee.web.ResourceMetadata;

public class StoreException extends HiveException {

	private static final long serialVersionUID = 1L;

	private final ResourceMetadata metadata;
	
	public StoreException(ResourceMetadata metadata, Throwable cause) {
		super(cause);
		this.metadata = metadata;
	}

	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("Failed to store resource from ").append(metadata.getLocation());
		return builder.toString();
	}
}
