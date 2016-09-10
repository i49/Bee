package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReporter implements BeeEventListener {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);

	@Override
	public void handleTaskEvent(Task task) {
		StringBuilder builder = new StringBuilder();
		int level = task.getLevel();
		while (level-- > 0) {
			builder.append("  ");
		}
		builder.append("@").append(task.getDistance());
		builder.append(" ").append(task.getPhase());
		builder.append("(").append("SUCCESS").append(")");
		builder.append(" ").append(task.getLocation());
		log.info(builder.toString());
	}
}
