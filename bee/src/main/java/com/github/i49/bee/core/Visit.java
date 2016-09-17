package com.github.i49.bee.core;

import com.github.i49.bee.web.ResourceMetadata;

public class Visit {

	private final int id;
	private final ResourceMetadata metadata;
	private boolean stored;
	
	public Visit(int id, ResourceMetadata metadata) {
		this.id = id;
		this.metadata = metadata;
		this.stored = false;
	}

	public int getId() {
		return id;
	}

	public ResourceMetadata getMetadata() {
		return metadata;
	}
	
	public boolean isStored() {
		return stored;
	}
	
	public void setStored() {
		this.stored = true;
	}
}
