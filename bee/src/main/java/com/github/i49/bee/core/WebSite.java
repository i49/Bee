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
	private final List<String> paths = new ArrayList<>();
	
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
	
	public List<String> getPaths() {
		return paths;
	}

	public boolean contains(URL location) {
		if (!location.getHost().equals(this.host)) {
			return false;
		}
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
	
	public boolean containsPath(String path) {
		if (this.paths.size() == 0) {
			return true;
		}
		for (String prefix : this.paths) {
			if (path.startsWith(prefix)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		if (port == -1)
			return host;
		else
			return host + ":" + port;
	}
}
