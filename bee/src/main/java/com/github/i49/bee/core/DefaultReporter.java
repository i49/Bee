package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReporter implements BeeEventListener {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);

	@Override
	public void handleResourceEvent(ResourceEvent e) {
		String indent = e.isSubordinate() ? "  " : "";
		String operation = e.getOperation().toString();
		String status = e.getStatus().toString();
		log.info(indent + "+" + e.getDistance() + " " + operation + "[" + status + "] " + e.getLocation());
	}
}
