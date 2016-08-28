package com.github.i49.bee.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultReporter implements BeeEventListener {

	private static final Log log = LogFactory.getLog(DefaultReporter.class);

	private static final Map<ResourceStatus, String> statusMap = new HashMap<>();
	
	static {
		statusMap.put(ResourceStatus.SUCCEEDED, "SUCCESS");
		statusMap.put(ResourceStatus.FAILED, "FAIL");
		statusMap.put(ResourceStatus.SKIPPED, "SKIP");
	}
	
	@Override
	public void handleResourceEvent(ResourceEvent e) {
		ResourceOperation operation = e.getOperation();
		String status = statusMap.get(e.getStatus());
		String indent = e.isSubordinate() ? "  " : "";
		if (operation == ResourceOperation.STORE) {
			log.info(indent + "@" + e.getDistance() + " STORE[" + status + "] " + e.getLocation());
		}
	}
}
