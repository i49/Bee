package com.github.i49.bee.core;

import java.util.ArrayList;
import java.util.List;

import com.github.i49.bee.web.Locator;

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

	public boolean contains(Locator location) {
		if (!location.getHost().equals(this.host))
			return false;
		if (this.port != -1) {
			int port = location.getPort();
			if (port == -1) {
				port = getDefaultPort(location.getScheme());
			}
			if (port != this.port) {
				return false;
			}
		}
		
		return containsPath(location.getPath());
	}
	
	private static int getDefaultPort(String scheme) {
		if (scheme.equals("http")) {
			return 80;
		} else if (scheme.equals("https")) {
			return 443;
		}
		return -1;
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
