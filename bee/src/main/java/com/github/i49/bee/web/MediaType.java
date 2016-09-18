package com.github.i49.bee.web;

import java.util.HashMap;
import java.util.Map;

public enum MediaType {
	
	TEXT_PLAIN("text", "plain"),
	TEXT_HTML("text", "html"),
	TEXT_CSS("text", "css"),
	TEXT_JAVASCRIPT("text", "javascript"),
	TEXT_TURTLE("text", "turtle"),
	APPLICATION_XHTML_XML("appliaction", "xhtml+xml"),
	APPLICATION_XML("application", "xml"),
	APPLICATION_JAVASCRIPT("application", "javascript"),
	APPLICATION_JSON("application", "json"),
	APPLICATION_RDF_XML("application", "rdf+xml"),
	IMAGE_PNG("image", "png"),
	IMAGE_JPEG("image", "jpeg"),
	IMAGE_GIF("image", "gif");

	private final String type;
	private final String subtype;
	
	private static final Map<String, MediaType> map = new HashMap<>();
	
	static {
		add(TEXT_PLAIN);
		add(TEXT_HTML);
		add(TEXT_CSS);
		add(TEXT_JAVASCRIPT);
		add(TEXT_TURTLE);
		add(APPLICATION_XHTML_XML);
		add(APPLICATION_XML);
		add(APPLICATION_JAVASCRIPT);
		add(APPLICATION_JSON);
		add(APPLICATION_RDF_XML);
		add(IMAGE_PNG);
		add(IMAGE_JPEG);
		add(IMAGE_GIF);
	}
	
	MediaType(String type, String subtype) {
		this.type = type;
		this.subtype = subtype;
	}

	public String getType() {
		return type;
	}

	public String getSubtype() {
		return subtype;
	}
	
	@Override
	public String toString() {
		return type + "/" + subtype;
	}

	public static MediaType of(String value) {
		if (value == null) {
			return null;
		}
		return map.get(value.trim());
	}
	
	private static void add(MediaType mediaType) {
		map.put(mediaType.toString(), mediaType);
	}
}
