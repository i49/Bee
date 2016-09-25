package com.github.i49.bee.core;

public class StatisticsCollector implements Statistics, BeeEventHandler {

	private int visits;
	private int visitsCompleted;
	private int maxDistance;

	private int resourcesToDownload;
	private int resourcesDownloaded;
	private int resourcesToStore;
	private int resourcesStored;
	
	public StatisticsCollector() {
	}

	@Override
	public int getVisits() {
		return visits;
	}

	@Override
	public int getVisitsCompleted() {
		return visitsCompleted;
	}

	@Override
	public int getMaxDistance() {
		return maxDistance;
	}

	@Override
	public int getResourcesToDownload() {
		return resourcesToDownload;
	}

	@Override
	public int getResourcesDownloaded() {
		return resourcesDownloaded;
	}

	@Override
	public int getResourcesToStore() {
		return resourcesToStore;
	}

	@Override
	public int getResourcesStored() {
		return resourcesStored;
	}
	
	@Override
	public void handleVisitStarted(Visit v) {
		++visits;
		if (maxDistance < v.getDistance()) {
			maxDistance = v.getDistance();
		}
	}

	@Override
	public void handleVisitCompleted(Visit v) {
		++visitsCompleted;
	}

	@Override
	public void handleDownloadStarted(Visit v) {
		++resourcesToDownload;
	}

	@Override
	public void handleDownloadCompleted(Visit v) {
		++resourcesDownloaded;
	}

	@Override
	public void handleStoreStarted(Visit v) {
		++resourcesToStore;
	}

	@Override
	public void handleStoreCompleted(Visit v) {
		++resourcesStored;
	}
}
