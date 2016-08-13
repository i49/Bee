package com.github.i49.bee.core;

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

	@Override
	public String toString() {
		if (port == -1)
			return host;
		else
			return host + ":" + port;
	}
}
