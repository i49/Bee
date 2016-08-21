package com.github.i49.bee.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReporter implements Reporter {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);
	
	@Override
	public void reportTaskResult(Task task) {
		log.info("[" + task.getDistance() + "]" + mapTaskStatus(task.getStatus()) + task.getLocation().toString());
	}

	@Override
	public void reportTotalResult(Statistics stat) {
		log.info("Successes: " + stat.getSuccesses() + " Failures: " + stat.getFailures());
	}
	
	private static String mapTaskStatus(Task.Status status) {
		switch (status) {
		case DONE:
			return "+";
		case FAILED:
			return "!";
		default:
			return " ";
		}
	}
}
