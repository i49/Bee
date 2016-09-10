package com.github.i49.bee.web;

/**
 * Exception thrown if retrieved web resource had unsupported media type.
 */
public class UnsupportedMediaException extends WebException {

	private static final long serialVersionUID = 1L;
	private final String mediaType;

	public UnsupportedMediaException(String mediaType) {
		this.mediaType = mediaType;
	}
	
	public String getMediaType() {
		return mediaType;
	}
	
	@Override
	public String getMessage() {
		return "Unsupported media type: " + getMediaType();
	}
}
