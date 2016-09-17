package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class TripTask extends Task {
	
	private final Trip trip;
	
	public TripTask(Trip trip) {
		this.trip = trip;
	}

	@Override
	protected boolean doBeforeSubtasks() {
		getVisitor().setCurrentTrip(this.trip);
		Locator location = Locator.parse(trip.getStartingPoint());
		if (location == null) {
			return false;
		}
		addSubtask(new ResourceTask(location, 0));
		return true;
	}
	
	@Override
	protected void doAfterSubtask(Task subtask) {
		ResourceTask resourceTask = (ResourceTask)subtask;
		addSubtasksFirst(resourceTask.getFutureTasks());
	}
}
