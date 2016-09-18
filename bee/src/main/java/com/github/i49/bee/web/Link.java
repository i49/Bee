package com.github.i49.bee.web;

import java.util.Set;

public class Link {
	
	private final Locator location;
	private final String element;
	private final Set<LinkType> linkTypes;
	private final MediaType mediaType;
	
	public Locator getLocation() {
		return location;
	}
	
	public String getElement() {
		return element;
	}
	
	public boolean hasLinkType(LinkType linkType) {
		if (this.linkTypes == null) {
			return false;
		}
		return this.linkTypes.contains(linkType);
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	protected Link(Locator location, String element, Set<LinkType> linkTypes, MediaType mediaType) {
		this.location = location;
		this.element = element;
		this.linkTypes = linkTypes;
		this.mediaType = mediaType;
	}

	public static Link create(Locator location, String element, Set<LinkType> linkTypes, MediaType mediaType) {
		if (location == null) {
			return null;
		}
		return new Link(location, element, linkTypes, mediaType);
	}
}
