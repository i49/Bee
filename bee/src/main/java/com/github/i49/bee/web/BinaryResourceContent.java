package com.github.i49.bee.web;

public class BinaryResourceContent implements ResourceContent {

	private final byte[] content;
	
	protected BinaryResourceContent(byte[] content) {
		this.content = content;
	}
	
	@Override
	public byte[] getBytes(ResourceSerializer serializer) {
		return content;
	}

	public static BinaryResourceContent create(byte[] content) {
		return new BinaryResourceContent(content);
	}
}
