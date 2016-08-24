package com.github.i49.bee.web;

import java.net.URI;

public class BinaryWebResource extends AbstractWebResource {

	private final byte[] content;
	
	protected BinaryWebResource(URI location, MediaType mediaType, byte[] content) {
		super(location, mediaType);
		this.content = content;
	}
	
	public static BinaryWebResource contentOf(URI location, MediaType mediaType, byte[] content) {
		return new BinaryWebResource(location, mediaType, content);
	}

	@Override
	public byte[] getContent(ResourceSerializer serializer) {
		return content;
	}
}
