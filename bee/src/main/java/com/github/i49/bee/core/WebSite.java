package com.github.i49.bee.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Web site allowed to visit.
 */
public class WebSite {

	private final String host;
	private final int port;
	private final List<String> includes = new ArrayList<>();
	private final List<String> excludes = new ArrayList<>();
	
	public WebSite(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}
	
	public List<String> getIncludes() {
		return includes;
	}
	
	public List<String> getExcludes() {
		return excludes;
	}

	public boolean contains(URL location) {
		if (!location.getHost().equals(this.host))
			return false;
		if (this.port != -1) {
			int port = location.getPort();
			if (port == -1) {
				port = location.getDefaultPort();
			}
			if (port != this.port) {
				return false;
			}
		}
		
		return containsPath(location.getPath());
	}
	
	private boolean containsPath(String path) {
		return (includes(path) && !excludes(path));
	}
	
	private boolean includes(String path) {
		if (this.includes.isEmpty()) {
			return true;
		}
		for (String directory : this.includes) {
			if (path.startsWith(directory)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean excludes(String path) {
		for (String directory : this.excludes) {
			if (path.startsWith(directory)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		if (port == -1) {
			return host;
		} else {
			return host + ":" + port;
		}
	}
}
