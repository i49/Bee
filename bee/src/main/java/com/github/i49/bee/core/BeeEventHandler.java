package com.github.i49.bee.core;

/**
 * The handler interface to receive events that will occur against Bee. 
 */
public interface BeeEventHandler {

	void handleDownloadStarted(Visit v);
	
	void handleDownloadCompleted(Visit v);
	
	void handleDownloadFailed(Visit v, Exception e);
	
	void handleStoreStarted(Visit v);
	
	void handleStoreCompleted(Visit v);
	
	void handleStoreFailed(Visit v, Exception e);
}
