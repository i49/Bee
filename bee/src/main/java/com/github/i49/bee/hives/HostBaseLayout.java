package com.github.i49.bee.hives;

import java.net.URI;

public class HostBaseLayout implements Layout {

	@Override
	public String mapPath(URI location) {
		StringBuilder builder = new StringBuilder("/");
		builder.append(location.getHost()).append("/");
		final int port = location.getPort();
		if (port >= 0) {
			builder.append(port);
		} else {
			builder.append("-");
		}
		builder.append(location.getPath());
		return builder.toString();
	}
}
