package com.github.i49.bee.web;

import java.util.HashMap;
import java.util.Map;

public enum MediaType {
	
	TEXT_PLAIN("text", "plain"),
	TEXT_HTML("text", "html"),
	APPLICATION_XHTML_XML("appliaction", "xhtml+xml"),
	APPLICATION_XML("application", "xml"),
	APPLICATION_JSON("application", "json");

	private final String type;
	private final String subtype;
	
	private static final Map<String, MediaType> map = new HashMap<>();
	
	static {
		add(TEXT_PLAIN);
		add(TEXT_HTML);
		add(APPLICATION_XHTML_XML);
		add(APPLICATION_XML);
		add(APPLICATION_JSON);
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
		return map.get(value.trim());
	}
	
	private static void add(MediaType mediaType) {
		map.put(mediaType.toString(), mediaType);
	}
}