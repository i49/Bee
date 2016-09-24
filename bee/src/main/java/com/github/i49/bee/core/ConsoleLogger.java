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
		StringBuilder b = builderFor(v);
		b.append("Downloading <").append(v.getLocation()).append(">");
		log.info(b.toString());
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		StringBuilder b = builderFor(v);
		b.append("Downloaded <").append(v.getLocation()).append(">");
		ResourceMetadata metadata = v.getFound().getMetadata();
		b.append(" (").append(metadata.getMediaType()).append(")");
		log.info(b.toString());
	}

	@Override
	public void handleDownloadFailed(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Failed to download <").append(v.getLocation()).append(">");
		log.error(b.toString());
	}
	
	@Override
	public void handleStoreCompleted(Visit v) {
		StringBuilder b = builderFor(v);
		b.append("Stored at \"").append(v.getFound().getLocalPath()).append("\"");
		log.info(b.toString());
	}
	
	@Override
	public void handleStoreFailed(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Failed to store at \"").append(v.getLocation()).append("\"");
		log.error(b.toString());
	}
	
	@Override
	public void handleVisitSkipped(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Skipped visited ").append("<").append(v.getLocation()).append(">");
		log.warn(b.toString());
	}
	
	@Override
	public void handleLinkStarted(Found f) {
		StringBuilder b = builderFor(f);
		b.append("Rewriting ");
		appendLinkLocation(b, f);
		log.info(b.toString());
	}

	@Override
	public void handleLinkFailed(Found f, Exception e) {
		StringBuilder b = builderFor(f);
		b.append("Failed to rewrite ");
		appendLinkLocation(b, f);
		log.error(b.toString());
	}
	
	private StringBuilder builderFor(Visit v) {
		StringBuilder b = new StringBuilder();
		b.append("[").append(v.getTripNo())
			.append(":").append(v.getVisitNo())
			.append(":").append(v.getDistance()).append("] ");
		return b;
	}
	
	private StringBuilder builderFor(Found f) {
		StringBuilder b = new StringBuilder();
		b.append("[").append(f.getResourceNo()).append("] ");
		return b;
	}
	
	private static void appendLinkLocation(StringBuilder b, Found f) {
		b.append("\"").append(f.getLocalPath()).append("\"");
	}
}
