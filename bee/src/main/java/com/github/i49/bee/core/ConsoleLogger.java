package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.ResourceMetadata;

/**
 * Default implementation of console logger.
 */
public class ConsoleLogger implements BeeEventHandler {

	private static final Log log = LogFactory.getLog(ConsoleLogger.class);

	@Override
	public void handleDownloadStarted(Visit v) {
		StringBuilder builder = builderFor(v);
		builder.append("Downloading <").append(v.getLocation()).append(">");
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		StringBuilder builder = builderFor(v);
		builder.append("Downloaded <").append(v.getLocation()).append(">");
		ResourceMetadata metadata = v.getFound().getMetadata();
		builder.append(" (").append(metadata.getMediaType()).append(")");
		log.info(builder.toString());
	}

	@Override
	public void handleDownloadFailed(Visit v, Exception e) {
		StringBuilder builder = builderFor(v);
		builder.append("Failed to download <").append(v.getLocation()).append(">");
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreCompleted(Visit v) {
		StringBuilder builder = builderFor(v);
		builder.append("Stored at \"").append(v.getFound().getLocalPath()).append("\"");
		log.info(builder.toString());
	}
	
	@Override
	public void handleStoreFailed(Visit v, Exception e) {
		StringBuilder builder = builderFor(v);
		builder.append("Failed to store at \"").append(v.getLocation()).append("\"");
		log.info(builder.toString());
	}
	
	@Override
	public void handleLinkStarted(Found f) {
		StringBuilder builder = builderFor(f);
		builder.append("Rewriting ");
		appendLocations(builder, f);
		log.info(builder.toString());
	}

	@Override
	public void handleLinkFailed(Found f, Exception e) {
		StringBuilder builder = builderFor(f);
		builder.append("Failed to rewrite ");
		appendLocations(builder, f);
		log.info(builder.toString());
	}
	
	private StringBuilder builderFor(Visit v) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(v.getTripNo())
			.append(":").append(v.getVisitNo())
			.append(":").append(v.getDistance()).append("] ");
		return builder;
	}
	
	private StringBuilder builderFor(Found f) {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(f.getResourceNo()).append("] ");
		return builder;
	}
	
	private static void appendLocations(StringBuilder builder, Found f) {
		builder
			.append("\"")
			.append(f.getLocalPath())
			.append("\" from <")
			.append(f.getMetadata().getLocation())
			.append(">");
	}
}
