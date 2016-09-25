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
	public void handleTripStarted(Trip trip) {
		StringBuilder b = builderFor(trip);
		b.append("Trip started from ")
			.append("<").append(trip.getStartingPoint()).append(">");
		log.info(b);
	}
	
	@Override
	public void handleTripCompleted(Trip trip, Statistics stats) {
		StringBuilder b = builderFor(trip);
		b.append("Trip completed: ");
		appendTripStatistics(b, trip, stats);
		log.info(b);
	}
	
	@Override
	public void handleVisitSkipped(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Skipped once visited ").append("<").append(v.getLocation()).append(">");
		log.warn(b);
	}
	
	@Override
	public void handleDownloadStarted(Visit v) {
		StringBuilder b = builderFor(v);
		b.append("Downloading <").append(v.getLocation()).append(">");
		log.info(b);
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		StringBuilder b = builderFor(v);
		b.append("Downloaded <").append(v.getLocation()).append(">");
		ResourceMetadata metadata = v.getFound().getMetadata();
		b.append(" (").append(metadata.getMediaType()).append(")");
		log.info(b);
	}

	@Override
	public void handleDownloadFailed(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Failed to download <").append(v.getLocation()).append(">");
		log.error(b);
	}
	
	@Override
	public void handleStoreCompleted(Visit v) {
		StringBuilder b = builderFor(v);
		b.append("Stored at \"").append(v.getFound().getLocalPath()).append("\"");
		log.info(b);
	}
	
	@Override
	public void handleStoreFailed(Visit v, Exception e) {
		StringBuilder b = builderFor(v);
		b.append("Failed to store at \"").append(v.getLocation()).append("\"");
		log.error(b);
	}
	
	@Override
	public void handleLinkStarted(Found f) {
		StringBuilder b = builderFor(f);
		b.append("Rewrites ");
		appendLinkLocation(b, f);
		log.info(b);
	}

	@Override
	public void handleLinkFailed(Found f, Exception e) {
		StringBuilder b = builderFor(f);
		b.append("Failed to rewrite ");
		appendLinkLocation(b, f);
		log.error(b);
	}
	
	private StringBuilder builderFor(Trip trip) {
		StringBuilder b = new StringBuilder();
		b.append("[").append(trip.getTripNo()).append("] ");
		return b;
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
	
	private static void appendTripStatistics(StringBuilder b, Trip trip, Statistics stats) {
		b.append("Visits: ");
		b.append(stats.getVisitsCompleted()).append("/").append(stats.getVisits());
		b.append(" Distance: ");
		b.append(stats.getMaxDistance()).append("/").append(trip.getDistanceLimit());
		b.append(" Downloads: ");
		b.append(stats.getResourcesDownloaded()).append("/").append(stats.getResourcesToDownload());
		b.append(" Stores: ");
		b.append(stats.getResourcesStored()).append("/").append(stats.getResourcesToStore());
	}
	
	private static void appendLinkLocation(StringBuilder b, Found f) {
		b.append("\"").append(f.getLocalPath()).append("\"");
	}
}
