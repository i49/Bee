package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.Locator;

public abstract class AbstractLayout implements Layout {

	private static Log log = LogFactory.getLog(AbstractLayout.class);
	
	private static final String DEFAULT_INDEX_NAME = "index.html";

	private String indexName = DEFAULT_INDEX_NAME;
	
	private final Map<Locator, String> cache = new HashMap<>();
	private final Set<String> directories = new HashSet<>();

	private final Map<String, DirectoryConfiguration> directoryConfigurations = new HashMap<>();
	
	protected AbstractLayout() {
	}
	
	@Override
	public void setIndexName(String name) {
		this.indexName = name;
	}
	
	@Override
	public boolean find(Locator remotePath) {
		if (remotePath == null) {
			return false;
		}
		return (this.cache.get(remotePath) != null);
	}
	
	@Override
	public String mapPath(Locator remotePath) {
		if (remotePath == null) {
			return null;
		}
		String localPath = cache.get(remotePath);
		if (localPath == null) {
			localPath = doMapPath(remotePath);
			if (localPath != null) {
				localPath = adjustPath(localPath);
				addPathToDirectories(localPath);
				addPathToCache(remotePath, localPath);
			}
		}
		return localPath;
	}
	
	@Override
	public DirectoryConfiguration getDirectoryConfiguration(String path) {
		DirectoryConfiguration c = this.directoryConfigurations.get(path);
		if (c == null) {
			c = new DirectoryConfigurationImpl(path);
			this.directoryConfigurations.put(path, c);
		}
		return c;
	}
	
	protected String adjustPath(String path) {
		if (!path.endsWith("/")) {
			String dir = path + "/";
			if (directories.contains(dir)) {
				path = dir;
				log.debug("Adjusted path: " + path);
			}
		}
		if (path.endsWith("/")) {
			path += this.indexName;
		}
		return path;
	}
	
	protected void addPathToDirectories(String path) {
		if (path.endsWith("/")) {
			addDirectoriesRecursively(path);
		} else {
			final int index = path.lastIndexOf("/");
			if (index >= 0) {
				String dir = path.substring(0, index + 1);
				addDirectoriesRecursively(dir);
			}
		}
	}
		
	protected void addDirectoriesRecursively(String dir) {
		if (!this.directories.contains(dir)) {
			this.directories.add(dir);
			final int index = dir.lastIndexOf("/");
			if (index >= 0) {
				addDirectoriesRecursively(dir.substring(0, index + 1));
			}
		}
	}

	protected void addPathToCache(Locator location, String path) {
		this.cache.put(location, path);
	}
	
	abstract protected String doMapPath(Locator location);

	private static class DirectoryConfigurationImpl implements DirectoryConfiguration {

		private final String path;
		private Map<String, Object> properties = new HashMap<>();
		
		public DirectoryConfigurationImpl(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
		public Map<String, Object> getProperties() {
			return properties;
		}
	}
}
