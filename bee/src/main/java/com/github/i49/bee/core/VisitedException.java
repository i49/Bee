package com.github.i49.bee.core;

class VisitedException extends Exception {

	private static final long serialVersionUID = 1L;
	private final Visit earlier;
	
	public VisitedException(Visit earlier) {
		this.earlier = earlier;
	}
	
	public Visit getEarlierVisit() {
		return earlier;
	}
}
