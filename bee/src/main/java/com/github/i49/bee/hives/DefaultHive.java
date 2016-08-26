package com.github.i49.bee.hives;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSource;
import com.github.i49.bee.web.Locator;
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
	public void store(WebResource resource, List<Link> links) throws IOException {
		String newLocation = this.layout.mapPath(resource.getLocation());
		if (links != null && !links.isEmpty()) {
			rewriteResource((LinkSource)resource, newLocation, links);
		}
		byte[] content = serializeResource(resource);
		this.storage.saveAt(newLocation, content);
	}
	
	protected byte[] serializeResource(WebResource resource) {
		return resource.getContent(this.serializer);
	}
	
	protected void rewriteResource(LinkSource resource, String newLocation, List<Link> links) {
		Map<Locator, Locator> map = createRewriteMap(newLocation, links);
		resource.rewriteLinks(map);
	}
	
	protected Map<Locator, Locator> createRewriteMap(String newLocation, List<Link> links) {
		Locator baseLocation = Locator.pathOf(newLocation).getParent();
		Map<Locator, Locator> map = new HashMap<>();
		for (Link link : links) {
			final Locator oldTarget = link.getLocation();
			final String mappedTarget = this.layout.mapPath(oldTarget);
			Locator targetLocation = Locator.pathOf(mappedTarget);
			Locator relativeLocation = baseLocation.relativize(targetLocation);
			map.put(oldTarget, relativeLocation);
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
