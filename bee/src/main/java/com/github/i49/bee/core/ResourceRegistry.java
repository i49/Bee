package com.github.i49.bee.core;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

public class ResourceRegistry {

	public static class Entry {
		
		private final int entryNo;
		private final ResourceMetadata metadata;
		
		public Entry(int entryNo, ResourceMetadata metadata) {
			this.entryNo = entryNo;
			this.metadata = metadata;
		}
		
		public int getEntryNo() {
			return entryNo;
		}
		
		public ResourceMetadata getMetadata() {
			return metadata;
		}
	};
	
	private final Map<Locator, Entry> entryMap = new HashMap<>();
	private int entryCount = 0;
	
	public ResourceRegistry() {
	}
	
	public Entry register(Locator location, ResourceMetadata metadata) {
		if (location == null || metadata == null) {
			throw new IllegalArgumentException();
		}
		Entry entry = find(location);
		if (entry != null) {
			return entry;
		}
		entry = find(metadata.getLocation());
		if (entry == null) {
			entry = new Entry(++entryCount, metadata);
			entryMap.put(metadata.getLocation(), entry);
		}
		if (!location.equals(metadata.getLastModified())) {
			entryMap.put(location, entry);
		}
		return entry;
	}

	public Entry find(Locator location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		return entryMap.get(location);
	}
}
