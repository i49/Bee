package com.github.i49.bee.hives.layouts;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;

public class HostBaseLayout extends AbstractLayout {

	@Override
	protected String doMapPath(Locator location, MediaType mediaType) {
		StringBuilder builder = new StringBuilder(createPrefix(location));
		builder.append(location.getPath());
		return builder.toString();
	}
	
	protected String createPrefix(Locator location) {
		StringBuilder builder = new StringBuilder("/");
		builder.append(location.getHost()).append("/");
		final int port = location.guessPort();
		if (port >= 0) {
			builder.append(port);
		} else {
			builder.append("-");
		}
		return builder.toString();
	}
}
