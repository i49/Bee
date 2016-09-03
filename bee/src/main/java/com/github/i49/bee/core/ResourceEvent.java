package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

public class ResourceEvent {

	private final Locator location;
	private final ResourceOperation operation;
	private int distance;
	private boolean subordinate;
	private int entryNo;
	private ResourceStatus status;
	
	public ResourceEvent(Locator location, ResourceOperation operation) {
		this.location = location;
		this.operation = operation;
	}
	
	public Locator getLocation() {
		return location;
	}

	public ResourceOperation getOperation() {
		return operation;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public boolean isSubordinate() {
		return subordinate;
	}

	public void setSubordinate(boolean subordinate) {
		this.subordinate = subordinate;
	}

	public int getEntryNo() {
		return entryNo;
	}

	public void setEntryNo(int entryNo) {
		this.entryNo = entryNo;
	}

	public ResourceStatus getStatus() {
		return status;
	}

	public void setStatus(ResourceStatus status) {
		this.status = status;
	}
}
