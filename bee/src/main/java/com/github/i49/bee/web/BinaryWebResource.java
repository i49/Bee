package com.github.i49.bee.web;

import java.io.InputStream;
import java.net.URI;

public class BinaryWebResource extends AbstractWebResource {

	protected BinaryWebResource(URI initialLocation, URI finalLocation, MediaType mediaType) {
		super(initialLocation, finalLocation, mediaType);
	}
	
	public static BinaryWebResource contentOf(URI initialLocation, URI finalLocation, MediaType mediaType, InputStream stream) {
		return new BinaryWebResource(initialLocation, finalLocation, mediaType);
	}
}
