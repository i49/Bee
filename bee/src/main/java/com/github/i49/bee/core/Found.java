package com.github.i49.bee.core;
import java.util.Collections;
import java.util.List;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

public class Found {

	private final long id;
	private final ResourceMetadata metadata;
	private List<Locator> hyperlinks;
	private List<Locator> externalResourceLinks;
	private String localPath;
	
	public Found(long id, ResourceMetadata metadata) {
		this.id = id;
		this.metadata = metadata;
		this.hyperlinks = Collections.emptyList();
		this.externalResourceLinks = Collections.emptyList();
	}

	public long getId() {
		return id;
	}
	
	public ResourceMetadata getMetadata() {
		return metadata;
	}

	public List<Locator> getHyperlinks() {
		return hyperlinks;
	}

	public void setHyperlinks(List<Locator> hyperlinks) {
		this.hyperlinks = hyperlinks;
	}

	public List<Locator> getExternalResourceLinks() {
		return externalResourceLinks;
	}

	public void setExternalResourceLinks(List<Locator> externalResourceLinks) {
		this.externalResourceLinks = externalResourceLinks;
	}

	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
