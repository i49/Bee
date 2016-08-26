package com.github.i49.bee.web;

public class BinaryWebResource extends AbstractWebResource {

	private final byte[] content;
	
	protected BinaryWebResource(Locator location, MediaType mediaType, byte[] content) {
		super(location, mediaType);
		this.content = content;
	}
	
	public static BinaryWebResource create(Locator location, MediaType mediaType, byte[] content) {
		return new BinaryWebResource(location, mediaType, content);
	}

	@Override
	public byte[] getContent(ResourceSerializer serializer) {
		return content;
	}
}
