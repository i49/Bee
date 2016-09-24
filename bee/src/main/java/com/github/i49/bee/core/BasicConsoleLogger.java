package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of console logger.
 */
public class BasicConsoleLogger implements BeeEventHandler {

	private static final Log log = LogFactory.getLog(BasicConsoleLogger.class);

	@Override
	public void handleDownloadStarted(Visit v) {
		StringBuilder builder = new StringBuilder("Downloading: ");
		appendLocationPart(builder, v);
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		StringBuilder builder = new StringBuilder("Downloaded: ");
		appendLocationPart(builder, v);
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadFailed(Visit v, Exception e) {
		StringBuilder builder = new StringBuilder("Failed to download: ");
		appendLocationPart(builder, v);
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreStarted(Visit v) {
	}
	
	@Override
	public void handleStoreCompleted(Visit v) {
		StringBuilder builder = new StringBuilder("Stored: ");
		appendLocationPart(builder, v);
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreFailed(Visit v, Exception e) {
		StringBuilder builder = new StringBuilder("Failed to store: ");
		appendLocationPart(builder, v);
		log.info(builder.toString());
	}

	protected void appendLocationPart(StringBuilder builder, Visit v) {
		appendDistance(builder, v.getDistance());
		builder.append(v.getLocation().toString());
		if (v.hasFound()) {
			builder.append(" (#").append(v.getFound().getId()).append(")");
		}
	}

	protected void appendDistance(StringBuilder builder, int distance) {
		builder.append("[").append(distance).append("]");
	}
}
