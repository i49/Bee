package com.github.i49.bee.hives;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.LinkSource;
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
	public void store(WebResource resource, List<URI> linkTargets) throws IOException {
		URI newLocation = this.layout.mapPath(resource.getLocation());
		if (linkTargets != null && !linkTargets.isEmpty()) {
//			rewriteResource((LinkSource)resource, newLocation, linkTargets);
		}
		byte[] content = serializeResource(resource);
		this.storage.saveAt(newLocation.getPath(), content);
	}
	
	protected byte[] serializeResource(WebResource resource) {
		return resource.getContent(this.serializer);
	}
	
	protected void rewriteResource(LinkSource resource, URI newLocation, List<URI> linkTargets) {
		Map<URI, URI> map = createRewriteMap(newLocation, linkTargets);
		resource.rewriteLinks(map);
	}
	
	protected Map<URI, URI> createRewriteMap(URI newLocation, List<URI> oldTargets) {
		Map<URI, URI> map = new HashMap<>();
		for (URI oldTarget : oldTargets) {
			URI newTarget = this.layout.mapPath(oldTarget);
			URI relative = newLocation.relativize(newTarget);
			map.put(oldTarget, relative);
		}
		return map;
	}

	protected Layout createDefaultLayout() {
		return new HostBaseLayout();
	}
	
	protected Storage createDefaultStorage() {
		return new DirectoryStorage();
	}
}
