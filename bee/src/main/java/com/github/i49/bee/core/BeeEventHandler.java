package com.github.i49.bee.core;

/**
 * The handler interface to receive events that will occur against Bee. 
 */
public interface BeeEventHandler {

	default void handleDownloadStarted(Visit v) {}
	
	default void handleDownloadCompleted(Visit v) {}
	
	default void handleDownloadFailed(Visit v, Exception e) {}
	
	default void handleStoreStarted(Visit v) {}
	
	default void handleStoreCompleted(Visit v) {}
	
	default void handleStoreFailed(Visit v, Exception e) {}
	
	default void handleVisitSkipped(Visit v, Exception e) {}
	
	default void handleLinkStarted(Found f) {}
	
	default void handleLinkCompleted(Found f) {}
	
	default void handleLinkFailed(Found f, Exception e) {}
}
