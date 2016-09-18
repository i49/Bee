package com.github.i49.bee.core;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.github.i49.bee.hives.Hive;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebDownloader;

public interface Visitor {
	
	boolean canVisit(Locator location);
	
	boolean canVisit(Locator location, int distance);
	
	boolean hasDone(Locator location);
	
	void addDone(Visit record);

	WebDownloader getDownloader();
	
	Hive getHive();
	
	VisitMap getVisitMap();
	
	Predicate<Link> getExternalResourceLinkPredicate();
	
	Predicate<Link> getHyperlinkPredicate();

	void notifyEvent(Consumer<BeeEventHandler> consumer);
}
