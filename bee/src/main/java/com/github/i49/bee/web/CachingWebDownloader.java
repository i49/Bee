package com.github.i49.bee.web;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.common.Directories;

public class CachingWebDownloader extends BasicWebDownloader {
	
	private static final Log log = LogFactory.getLog(CachingWebDownloader.class);

	private final Path pathToCache;
	private final Map<Locator, CacheEntry> entries = new HashMap<>(); 
	
	public CachingWebDownloader(Path pathToCache) throws IOException {
		super();
		this.pathToCache = pathToCache.toAbsolutePath();
		createCacheDirectory(this.pathToCache);
	}
	
	@Override
	public void close() throws Exception {
		super.close();
		log.debug("Deleting cache directory: " + pathToCache);
		Directories.remove(pathToCache);
	}

	@Override
	public WebResource download(Locator location) throws Exception {
		WebResource resource = restoreResource(location);
		if (resource == null) {
			resource = super.download(location);
		}
		return resource;
	}

	@Override
	protected WebResource createResource(Locator initialLocation, ResourceMetadata metadata, byte[] content) throws Exception {
		WebResource resource = super.createResource(initialLocation, metadata, content);
		storeToCache(initialLocation, metadata, content);
		return resource;
	}
	
	private void createCacheDirectory(Path pathToCache) throws IOException {
		if (Files.exists(pathToCache)) {
			log.debug("Deleting old cache directory: " + pathToCache);
			if (Files.isDirectory(pathToCache)) {
				Directories.remove(pathToCache);
			} else {
				throw new NotDirectoryException(pathToCache + " is not directory.");
			}
		}
		log.debug("Creating cache directory: " + pathToCache);
		Files.createDirectories(pathToCache);
	}
	
	private WebResource restoreResource(Locator location) throws Exception {
		CacheEntry entry = entries.get(location);
		if (entry == null) {
			return null;
		}
		byte[] content = Files.readAllBytes(entry.getLocalPath());
		WebResource resource = super.createResource(location, entry.getMetadata(), content);
		log.debug("Restored from local cache: " + location);
		return resource;
	}
	
	private void storeToCache(Locator initialLocation, ResourceMetadata metadata, byte[] content) throws IOException {
		Files.createDirectories(pathToCache);
		CacheEntry entry = entries.get(metadata.getLocation());
		if (entry == null) {
			Path localPath = Files.createTempFile(this.pathToCache, "", "");
			entry = new CacheEntry(metadata, localPath);
		}
		Files.write(entry.getLocalPath(), content);
		this.entries.put(metadata.getLocation(), entry);
		if (!initialLocation.equals(metadata.getLocation())) {
			this.entries.put(initialLocation, entry);
		}
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
