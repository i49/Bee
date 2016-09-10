package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default implementation of console logger.
 */
public class DefaultReporter implements BeeEventListener {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);

	@Override
	public void handleTaskEvent(Task task) {
		reportTask(task, "SUCCESS");
	}

	@Override
	public void handleTaskFailure(Task task) {
		reportTask(task, "FAILED");
	}
	
	protected void reportTask(Task task, String status) {
		StringBuilder builder = new StringBuilder();
		int count = task.getDistance();
		while (count-- > 0) {
			builder.append("-");
		}
		builder.append("[").append(task.getDistance()).append("]");
		builder.append(" ").append(task.getPhase());
		builder.append("(").append(status).append(")");
		builder.append(" ").append(task.getLocation());
		log.info(builder.toString());
	}
}
