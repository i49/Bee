package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.hives.layouts.Layout;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceSerializer;
import com.github.i49.bee.web.WebResource;

public abstract class AbstractHive implements Hive {
	
	private static final Log log = LogFactory.getLog(AbstractHive.class);
	private static final String DEFAULT_BASE_PATH = "hive";
	private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;
	
	private Path basePath;
	private boolean clean;
	private Charset encoding;
	private Layout layout;
	private Storage storage;
	private ResourceSerializer serializer;
	
	protected AbstractHive() {
		this.basePath = Paths.get(DEFAULT_BASE_PATH).toAbsolutePath();
		this.clean = true;
		this.encoding = DEFAULT_ENCODING;
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
	public void open() throws IOException {
		if (this.layout == null) {
			this.layout = createDefaultLayout();
		}
		if (this.storage == null) {
			this.storage = createDefaultStorage();
		}
		this.storage.open(this.basePath, this.clean);
		this.serializer = new DefaultResourceSerializer(this.encoding);
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
	public String store(WebResource resource) throws HiveException {
		try {
			return tryStore(resource);
		} catch (Exception e) {
			throw new StoreException(resource.getMetadata(), e);
		}
	}

	@Override
	public Linker createLinker(Map<Locator, Locator> redirections) {
		return new BasicLinker(redirections, this.layout, this.storage, this.serializer);
	}
	
	private String tryStore(WebResource resource) throws Exception {
		final String localPath = this.layout.mapPath(resource.getMetadata().getLocation());
		byte[] bytes = serializeResource(resource);
		FileTime lastModified = FileTime.from(resource.getMetadata().getLastModified().toInstant());
		this.storage.write(localPath, bytes, lastModified);
		return localPath;
	}

	private byte[] serializeResource(WebResource resource) {
		return resource.getBytes(this.serializer);
	}
	
	protected abstract Layout createDefaultLayout();

	protected abstract Storage createDefaultStorage();
}
