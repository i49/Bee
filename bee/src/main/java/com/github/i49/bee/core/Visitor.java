package com.github.i49.bee.core;

import java.util.function.Consumer;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;

public interface Visitor {
	
	boolean canVisit(Locator location);
	
	boolean canVisit(Locator location, int distance);
	
	boolean hasDone(Locator location);
	
	void addDone(ResourceRecord record);

	Trip getCurrentTrip();

	void setCurrentTrip(Trip trip);
	
	WebDownloader getDownloader();
	
	Hive getHive();
	
	ResourceRegistry getRegistry();

	void notifyEvent(Consumer<BeeEventListener> consumer);
}
