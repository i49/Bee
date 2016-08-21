package com.github.i49.bee.core;

public interface Reporter {

	void reportTaskResult(Task task);
	
	void reportTotalResult(Statistics stat);
}
