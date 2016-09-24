package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of console logger.
 */
public class ConsoleLogger implements BeeEventHandler {

	private static final Log log = LogFactory.getLog(ConsoleLogger.class);

	@Override
	public void handleDownloadStarted(Visit v) {
		StringBuilder builder = builder(v);
		builder.append("Downloading from ").append(v.getLocation());
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		StringBuilder builder = builder(v);
		builder.append("Downloaded from ").append(v.getLocation());
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadFailed(Visit v, Exception e) {
		StringBuilder builder = builder(v);
		builder.append("Failed to download from ").append(v.getLocation());
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreStarted(Visit v) {
	}
	
	@Override
	public void handleStoreCompleted(Visit v) {
		StringBuilder builder = builder(v);
		builder.append("Stored at ").append(v.getFound().getLocalPath());
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreFailed(Visit v, Exception e) {
		StringBuilder builder = builder(v);
		builder.append("Failed to store ").append(v.getLocation());
		log.info(builder.toString());
	}

	protected StringBuilder builder(Visit v) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(v.getTripNo())
			.append(":").append(v.getVisitNo())
			.append(":").append(v.getDistance()).append("] ");
		return builder;
	}
}
