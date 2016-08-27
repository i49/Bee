package com.github.i49.bee.hives;

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
	
	protected AbstractLayout() {
	}
	
	@Override
	public String mapPath(Locator location) {
		if (location == null) {
			return null;
		}
		String path = cache.get(location);
		if (path == null) {
			path = doMapPath(location);
			if (path != null) {
				path = adjustPath(path);
				addPathToDirectories(path);
				addPathToCache(location, path);
			}
		}
		return path;
	}
	
	@Override
	public void setIndexName(String name) {
		this.indexName = name;
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
}