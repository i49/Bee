package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class TripTask extends Task {
	
	private final Trip trip;
	
	public TripTask(Trip trip) {
		this.trip = trip;
	}

	@Override
	protected boolean runBeforeSubtasks() {
		Locator location = Locator.parse(trip.getStartingPoint());
		if (location == null) {
			return false;
		}
		addSubtask(new ResourceTask(location, 0));
		return true;
	}
	
	@Override
	protected void runAfterEachSubtask(Task subtask) {
		ResourceTask resourceTask = (ResourceTask)subtask;
		addSubtasksFirst(resourceTask.getFutureTasks());
	}
}
