package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.Locator;

/**
 * Default implementation of console logger.
 */
public class BasicConsoleLogger implements BeeEventHandler {

	private static final Log log = LogFactory.getLog(BasicConsoleLogger.class);

	@Override
	public void handleDownloadStarted(int distance, int level, Locator location) {
		StringBuilder builder = new StringBuilder("Downloading: ");
		appendLocationPart(builder, distance, level, location);
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadCompleted(int distance, int level, Visit visit) {
		StringBuilder builder = new StringBuilder("Downloaded: ");
		appendLocationPart(builder, distance, level, visit);
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadFailed(int distance, int level, Locator location, Exception e) {
		StringBuilder builder = new StringBuilder("Failed to download: ");
		appendLocationPart(builder, distance, level, location);
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreStarted(int distance, int level, Visit visit) {
	}
	
	@Override
	public void handleStoreCompleted(int distance, int level, Visit visit) {
		StringBuilder builder = new StringBuilder("Stored: ");
		appendLocationPart(builder, distance, level, visit);
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreFailed(int distance, int level, Visit visit, Exception e) {
		StringBuilder builder = new StringBuilder("Failed to store: ");
		appendLocationPart(builder, distance, level, visit);
		log.info(builder.toString());
	}

	protected void appendLocationPart(StringBuilder builder, int distance, int level, Locator location) {
		appendDistance(builder, distance, level);
		builder.append(location.toString());
	}
	
	protected void appendLocationPart(StringBuilder builder, int distance, int level, Visit visit) {
		appendDistance(builder, distance, level);
		builder.append(visit.getMetadata().getLocation().toString());
		builder.append(" (#").append(visit.getId()).append(")");
	}

	protected void appendDistance(StringBuilder builder, int distance, int level) {
		builder.append("[");
		builder.append(distance - level);
		if (level > 0) {
			builder.append("+").append(level);
		}
		builder.append("]");
	}
}
