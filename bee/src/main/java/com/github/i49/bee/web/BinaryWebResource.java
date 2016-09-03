package com.github.i49.bee.web;

public class BinaryWebResource extends AbstractWebResource {

	private final byte[] content;
	
	@Override
	public byte[] getBytes(ResourceSerializer serializer) {
		return content;
	}

	public static BinaryWebResource create(ResourceMetadata metadata, byte[] content) {
		return new BinaryWebResource(metadata, content);
	}

	protected BinaryWebResource(ResourceMetadata metadata, byte[] content) {
		super(metadata);
		this.content = content;
	}
}
