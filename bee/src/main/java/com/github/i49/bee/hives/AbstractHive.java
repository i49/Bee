package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.i49.bee.web.LinkSource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.ResourceSerializer;
import com.github.i49.bee.web.WebResource;

public abstract class AbstractHive implements Hive {

	private static final String DEFAULT_BASE_PATH = "hive";
	
	private String basePath = DEFAULT_BASE_PATH;
	
	private Layout layout;
	private Storage storage;
	private ResourceSerializer serializer = new DefaultResourceSerializer();

	protected AbstractHive() {
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
	public void store(WebResource resource, List<ResourceMetadata> links) throws IOException {
		String newLocation = this.layout.mapPath(resource.getMetadata().getFinalLocation());
		if (links != null && !links.isEmpty()) {
			rewriteResource((LinkSource)resource, newLocation, links);
		}
		byte[] content = serializeResource(resource);
		FileTime lastModified = FileTime.from(resource.getMetadata().getLastModified().toInstant());
		this.storage.saveAt(newLocation, content, lastModified);
	}
	
	protected byte[] serializeResource(WebResource resource) {
		return resource.getContent(this.serializer);
	}
	
	protected void rewriteResource(LinkSource resource, String newLocation, List<ResourceMetadata> links) {
		Map<Locator, Locator> map = createRewriteMap(newLocation, links);
		resource.rewriteLinks(map);
	}
	
	protected Map<Locator, Locator> createRewriteMap(String newLocation, List<ResourceMetadata> links) {
		Locator baseLocation = Locator.pathOf(newLocation).getParent();
		Map<Locator, Locator> map = new HashMap<>();
		for (ResourceMetadata link : links) {
			final Locator target = link.getFinalLocation();
			final String mappedTarget = this.layout.mapPath(target);
			Locator targetLocation = Locator.pathOf(mappedTarget);
			Locator relativeLocation = baseLocation.relativize(targetLocation);
			map.put(link.getLocation(), relativeLocation);
		}
		return map;
	}

	protected abstract Layout createDefaultLayout();

	protected abstract Storage createDefaultStorage();
}
