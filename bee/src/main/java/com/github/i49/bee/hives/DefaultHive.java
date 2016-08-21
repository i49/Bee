package com.github.i49.bee.hives;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.WebResource;

public class DefaultHive implements Hive {

	private static final Log log = LogFactory.getLog(DefaultHive.class);

	private static final String DEFAULT_BASE_PATH = "hive";
	
	private String basePath = DEFAULT_BASE_PATH;
	
	private Layout layout;
	private Storage storage;
	
	public DefaultHive() {
	}
	
	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public void open() {
		if (this.layout == null) {
			this.layout = createDefaultLayout();
		}
		if (this.storage == null) {
			this.storage = createDefaultStorage();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public void store(WebResource resource) {
		String path = this.layout.mapPath(resource.getInitialLocation());
		log.debug("Stored at " + path);
	}

	protected Layout createDefaultLayout() {
		return new HostBaseLayout();
	}
	
	protected Storage createDefaultStorage() {
		return new DirectoryStorage();
	}
}
