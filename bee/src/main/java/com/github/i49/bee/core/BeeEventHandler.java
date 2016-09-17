package com.github.i49.bee.core;

import com.github.i49.bee.web.Locator;

/**
 * The handler interface to receive events that will occur against Bee. 
 */
public interface BeeEventHandler {

	void handleDownloadStarted(int distance, int level, Locator location);
	
	void handleDownloadCompleted(int distance, int level, Visit visit);
	
	void handleDownloadFailed(int distance, int level, Locator location, Exception e);
	
	void handleStoreStarted(int distance, int level, Visit visit);
	
	void handleStoreCompleted(int distance, int level, Visit visit);
	
	void handleStoreFailed(int distance, int level, Visit visit, Exception e);
}
