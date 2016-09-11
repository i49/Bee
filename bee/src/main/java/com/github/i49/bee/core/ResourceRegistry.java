package com.github.i49.bee.core;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

public class ResourceRegistry {

	private final Map<Locator, ResourceRecord> resourceMap = new HashMap<>();
	private int nextId = 1;
			
	public ResourceRegistry() {
	}
	
	public ResourceRecord register(Locator location, ResourceMetadata metadata) {
		if (location == null || metadata == null) {
			throw new IllegalArgumentException();
		}
		ResourceRecord entry = find(location);
		if (entry != null) {
			return entry;
		}
		entry = find(metadata.getLocation());
		if (entry == null) {
			entry = new ResourceRecord(this.nextId++, metadata);
			resourceMap.put(metadata.getLocation(), entry);
		}
		if (!location.equals(metadata.getLastModified())) {
			resourceMap.put(location, entry);
		}
		return entry;
	}

	public ResourceRecord find(Locator location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		return resourceMap.get(location);
	}
}
