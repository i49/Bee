package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class ExternalResourceTask extends ResourceTask {

	public ExternalResourceTask(Locator location, int distance, int level) {
		super(location, distance, level);
	}

	@Override
	protected void parseResource() {
	}
}
