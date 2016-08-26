package com.github.i49.bee.hives;

import com.github.i49.bee.web.Locator;

public class HostBaseLayout implements Layout {

	private static final String DEFAULT_INDEX_NAME = "index.html";
	
	private String indexName = DEFAULT_INDEX_NAME;

	@Override
	public String mapPath(Locator location) {
		StringBuilder builder = new StringBuilder("/");
		builder.append(location.getHost()).append("/");
		final int port = location.getPort();
		if (port >= 0) {
			builder.append(port);
		} else {
			builder.append("-");
		}
		final String path = location.getPath();
		builder.append(path);
		if (path.endsWith("/")) {
			builder.append(this.indexName);
		}
		return builder.toString();
	}
}
