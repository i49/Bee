package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReporter implements BeeEventListener {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);

	@Override
	public void handleResourceEvent(ResourceEvent e) {
		StringBuilder builder = new StringBuilder();
		if (e.isSubordinate()) {
			builder.append("  ");
		}
		if (e.getEntryNo() >= 0) {
			builder.append("#").append(e.getEntryNo());
		} else {
			builder.append("#-");
		}
		builder.append("@").append(e.getDistance());
		builder.append(" ").append(e.getOperation());
		builder.append("(").append(e.getStatus()).append(")");
		builder.append(" ").append(e.getLocation());
		log.info(builder.toString());
	}
}
