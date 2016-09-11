package com.github.i49.bee.core;

import java.util.function.Consumer;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;

public interface BeeContext {

	WebDownloader getDownloader();
	
	Hive getHive();
	
	ResourceRegistry getRegistry();
	
	boolean allowsToVisit(Locator location);
	
	void notifyEvent(Consumer<BeeEventListener> consumer);
}
