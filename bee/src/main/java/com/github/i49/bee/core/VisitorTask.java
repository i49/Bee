package com.github.i49.bee.core;

public class VisitorTask extends Task<Visitor> {

	public VisitorTask() {
	}

	public VisitorTask(Visitor visitor) {
		super(visitor);
	}
	
	public Visitor getVisitor() {
		return getContext();
	}
}
