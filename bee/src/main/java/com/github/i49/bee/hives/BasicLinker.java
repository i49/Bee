package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.hives.layouts.Layout;
import com.github.i49.bee.web.HtmlWebResource;
import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.LinkSourceResource;
import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;
import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.ResourceSerializer;
import com.github.i49.bee.web.WebContentException;
import com.github.i49.bee.web.WebResource;

public class BasicLinker implements Linker {

	private final Map<Locator, Locator> rediections;
	private final Layout layout;
	private final Storage storage;
	private final ResourceSerializer serializer;
	
	public BasicLinker(Map<Locator, Locator> redirections, Layout layout, Storage storage, ResourceSerializer serializer) {
		this.rediections = redirections;
		this.layout = layout;
		this.storage = storage;
		this.serializer = serializer;
	}

	@Override
	public void link(String localPath, ResourceMetadata metadata) throws IOException, WebContentException {
		if (localPath == null) {
			throw new IllegalArgumentException("path is null");
		}
		if (metadata == null) {
			throw new IllegalArgumentException("metadata is null");
		}
		MediaType mediaType = metadata.getMediaType();
		if (mediaType != MediaType.TEXT_HTML && mediaType != MediaType.APPLICATION_XHTML_XML) {
			throw new IllegalStateException("Invalid media type " + mediaType);
		}
		HtmlWebResource resource = deserializeResource(metadata, this.storage.read(localPath));
		FileTime lastModified = this.storage.getLastModifiedTime(localPath);
		rewriteResource(localPath, resource);
		byte[] bytes = serializeResource(resource);
		this.storage.write(localPath, bytes, lastModified);
	}
	
	private void rewriteResource(String localPath, LinkSourceResource resource) {
		Collection<Link> links = resource.getLinks();
		Map<Locator, Locator> rewriteMap = createRewriteMap(localPath, links);
		resource.rewriteLinks(rewriteMap);
	}
	
	private Map<Locator, Locator> createRewriteMap(String sourcePath, Collection<Link> links) {
		Locator basePath = Locator.pathOf(sourcePath).getParent();
		Map<Locator, Locator> map = new HashMap<>();
		for (Link link  : links) {
			Locator remote = link.getLocation();
			if (this.layout.find(remote)) {
				final String local = this.layout.mapPath(getRedirected(remote));
				Locator targetPath = Locator.pathOf(local);
				Locator relativePath = basePath.relativize(targetPath);
				map.put(remote, relativePath);
			}
		}
		return map;
	}

	private Locator getRedirected(Locator location) {
		Locator redirected = this.rediections.get(location);
		return (redirected != null) ? redirected : location;
	}
	
	private byte[] serializeResource(WebResource resource) {
		return resource.getBytes(this.serializer);
	}
	
	private HtmlWebResource deserializeResource(ResourceMetadata metadata, byte[] content) throws WebContentException {
		Charset encoding = this.serializer.getEncoding();
		return HtmlWebResource.create(metadata, content, encoding.name());
	}
}
