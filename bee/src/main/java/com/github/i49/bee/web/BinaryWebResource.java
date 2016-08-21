package com.github.i49.bee.web;

import java.net.URI;

public class BinaryWebResource extends AbstractWebResource {

	private final byte[] content;
	
	protected BinaryWebResource(URI initialLocation, URI finalLocation, MediaType mediaType, byte[] content) {
		super(initialLocation, finalLocation, mediaType);
		this.content = content;
	}
	
	public static BinaryWebResource contentOf(URI initialLocation, URI finalLocation, MediaType mediaType, byte[] content) {
		return new BinaryWebResource(initialLocation, finalLocation, mediaType, content);
	}

	@Override
	public byte[] getContent() {
		return content;
	}
}
