package com.github.i49.bee.core;

import java.util.function.Consumer;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;

public interface Visitor {

	WebDownloader getDownloader();
	
	Hive getHive();
	
	ResourceRegistry getRegistry();
	
	boolean canVisit(Locator location);
	
	boolean canVisit(Locator location, int distance);
	
	Trip getCurrentTrip();

	void setCurrentTrip(Trip trip);
	
	void notifyEvent(Consumer<BeeEventListener> consumer);
}
