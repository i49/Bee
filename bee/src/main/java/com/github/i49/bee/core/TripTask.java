package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class TripTask extends Task<BeeContext> {
	
	private final Seed seed;
	
	public TripTask(Seed seed) {
		this.seed = seed;
	}

	@Override
	protected boolean doBeforeSubtasks() {
		Locator location = Locator.parse(seed.getLocation());
		if (location == null) {
			return false;
		}
		addSubtask(new ResourceTask(location, 0));
		return true;
	}
}
