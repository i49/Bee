package com.github.i49.bee.core;

/**
 * The listener interface to receive events that will occur against Bee. 
 */
public interface BeeEventListener {

	void handleTaskEvent(Task task);
	
	void handleTaskFailure(Task task);
}
