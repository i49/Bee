package com.github.i49.bee.hives;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.ResourceSerializer;
import com.github.i49.bee.web.WebResource;

public class DefaultHive implements Hive {

	private static final Log log = LogFactory.getLog(DefaultHive.class);

	private static final String DEFAULT_BASE_PATH = "hive";
	
	private String basePath = DEFAULT_BASE_PATH;
	
	private Layout layout;
	private Storage storage;
	private ResourceSerializer serializer = new DefaultResourceSerializer();
	
	public DefaultHive() {
	}
	
	@Override
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public void open() throws IOException {
		if (this.layout == null) {
			this.layout = createDefaultLayout();
		}
		if (this.storage == null) {
			this.storage = createDefaultStorage();
		}
		this.storage.open(this.basePath);
	}

	@Override
	public void close() throws IOException {
		this.storage.close();
	}

	@Override
	public void store(WebResource resource) throws IOException {
		String path = this.layout.mapPath(resource.getLocation());
		byte[] content = serializeResource(resource);
		this.storage.saveAt(path, content);
	}
	
	protected byte[] serializeResource(WebResource resource) {
		return resource.getContent(this.serializer);
	}

	protected Layout createDefaultLayout() {
		return new HostBaseLayout();
	}
	
	protected Storage createDefaultStorage() {
		return new DirectoryStorage();
	}
}
