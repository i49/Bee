package com.github.i49.bee.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.hives.DefaultResourceSerializer;

public class CachingWebDownloader implements WebDownloader {

	private final WebDownloader downloader;
	private final Path pathToCache;

	private final Map<Locator, CacheEntry> entries = new HashMap<>(); 
	private final ResourceSerializer serializer = new DefaultResourceSerializer();
	
	public CachingWebDownloader(WebDownloader downloader, Path pathToCache) {
		this.downloader = downloader;
		this.pathToCache = pathToCache;
	}
	
	@Override
	public void close() throws Exception {
		downloader.close();
	}

	@Override
	public WebResource download(Locator location) throws Exception {
		WebResource resource = loadFromCache(location);
		if (resource == null) {
			resource = downloader.download(location);
			if (resource != null) {
				storeToCache(resource);
			}
		}
		return resource;
	}
	
	private WebResource loadFromCache(Locator location) {
		CacheEntry entry = this.entries.get(location);
		if (entry == null) {
			return null;
		}
		return null;
	}

	private void storeToCache(WebResource resource) throws IOException {
		Files.createDirectories(pathToCache);
		Path localPath = Files.createTempFile(this.pathToCache, "", "");
		CacheEntry entry = new CacheEntry(resource.getMetadata(), localPath);
		this.entries.put(entry.getMetadata().getLocation(), entry);
	}
	
	private static class CacheEntry {

		private final ResourceMetadata metadata;
		private final Path localPath;
		
		public CacheEntry(ResourceMetadata metadata, Path localPath) {
			this.metadata = metadata;
			this.localPath = localPath;
		}
		
		public ResourceMetadata getMetadata() {
			return metadata;
		}
		
		public Path getLocalPath() {
			return localPath;
		}
	}
}
