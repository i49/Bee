package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.ResourceSerializer;
import com.github.i49.bee.web.WebResource;

public abstract class AbstractHive implements Hive {
	
	private static final Log log = LogFactory.getLog(AbstractHive.class);
	private static final String DEFAULT_BASE_PATH = "hive";
	
	private Path basePath;
	private boolean clean;
	private Layout layout;
	private Storage storage;
	private ResourceSerializer serializer = new DefaultResourceSerializer();

	protected AbstractHive() {
		this.basePath = Paths.get(DEFAULT_BASE_PATH).toAbsolutePath();
		this.clean = true;
	}

	@Override
	public Path getBasePath() {
		return basePath;
	}

	@Override
	public void setBasePath(Path basePath) {
		this.basePath = basePath.toAbsolutePath();
	}
	
	@Override
	public Storage getStorage() {
		return storage;
	}
	
	@Override
	public void setStorage(Storage storage) {
		this.storage = storage;
	}
	
	@Override
	public void open() throws HiveException {
		if (this.layout == null) {
			this.layout = createDefaultLayout();
		}
		if (this.storage == null) {
			this.storage = createDefaultStorage();
		}
		this.storage.open(this.basePath, this.clean);
	}

	@Override
	public void close() {
		try {
			this.storage.close();
			log.debug("Hive storage was closed gracefully.");
		} catch (IOException e) {
			// Ignores exception
			log.debug("Failed to close hive storage: " + e.getMessage());
		}
	}

	@Override
	public void store(WebResource resource, Map<Locator, ResourceMetadata> links) throws HiveException {
		String newLocation = this.layout.mapPath(resource.getMetadata().getLocation());
		if (resource instanceof LinkSourceResource && links != null && !links.isEmpty()) {
			rewriteResource((LinkSourceResource)resource, newLocation, links);
		}
		byte[] bytes = serializeResource(resource);
		FileTime lastModified = FileTime.from(resource.getMetadata().getLastModified().toInstant());
		this.storage.saveAt(newLocation, bytes, lastModified);
	}
	
	protected byte[] serializeResource(WebResource resource) {
		return resource.getBytes(this.serializer);
	}
	
	protected void rewriteResource(LinkSourceResource resource, String newLocation, Map<Locator, ResourceMetadata> links) {
		Map<Locator, Locator> map = createRewriteMap(newLocation, links);
		resource.rewriteLinks(map);
	}
	
	protected Map<Locator, Locator> createRewriteMap(String newLocation, Map<Locator, ResourceMetadata> links) {
		Locator baseLocation = Locator.pathOf(newLocation).getParent();
		Map<Locator, Locator> map = new HashMap<>();
		for (Locator link  : links.keySet()) {
			final Locator target = links.get(link).getLocation();
			final String mappedTarget = this.layout.mapPath(target);
			Locator targetLocation = Locator.pathOf(mappedTarget);
			Locator relativeLocation = baseLocation.relativize(targetLocation);
			map.put(link, relativeLocation);
		}
		return map;
	}

	protected abstract Layout createDefaultLayout();

	protected abstract Storage createDefaultStorage();
}
