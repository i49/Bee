package com.github.i49.bee.core;
import java.util.Collections;
import java.util.List;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.ResourceMetadata;

/**
 * A resource found by bee.
 */
public class Found {

	private final int resourceNo;
	private final ResourceMetadata metadata;

	private boolean linkSource;
	private List<Locator> hyperlinks;
	private List<Locator> externalResourceLinks;
	private String localPath;
	
	public Found(int resourceNo, ResourceMetadata metadata) {
		this.resourceNo = resourceNo;
		this.metadata = metadata;
		this.linkSource = false;
		this.hyperlinks = Collections.emptyList();
		this.externalResourceLinks = Collections.emptyList();
	}

	/**
	 * Returns sequence number assigned to this found.
	 * @return sequence number assigned
	 */
	public int getResourceNo() {
		return resourceNo;
	}
	
	public ResourceMetadata getMetadata() {
		return metadata;
	}
	
	public boolean isLinkSource() {
		return linkSource;
	}
	
	public Found markAsLinkSource() {
		this.linkSource = true;
		return this;
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

	/**
	 * Returns local path of found resource.
	 * @return local path of found resource
	 */
	public String getLocalPath() {
		return localPath;
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
