package com.github.i49.bee.web;

public class MediaType {
	
	public static final MediaType TEXT_PLAIN = new MediaType("text", "plain");
	public static final MediaType TEXT_HTML = new MediaType("text", "html");
	public static final MediaType APPLICATION_XHTML_XML = new MediaType("appliaction", "xhtml+xml");
	public static final MediaType APPLICATION_XML = new MediaType("application", "xml");
	public static final MediaType APPLICATION_JSON = new MediaType("application", "json");

	private final String type;
	private final String subtype;
	
	protected MediaType(String type, String subtype) {
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

	public static MediaType valueOf(String value) {
		String[] tokens = value.split("/");
		if (tokens.length != 2) {
			return null;
		}
		return findMediaType(tokens[0].trim(), tokens[1].trim());
	}
	
	private static MediaType findMediaType(String type, String subtype) {
		if (type.equals("text")) {
			if (subtype.equals("plain"))
				return TEXT_PLAIN;
			else if (subtype.equals("html"))
				return TEXT_HTML;
		} else if (type.equals("application")) {
			if (subtype.equals("xhtml+xml")) {
				return APPLICATION_XHTML_XML;
			} else if (subtype.equals("xml")) {
				return APPLICATION_XML;
			} else if (subtype.equals("json")) {
				return APPLICATION_JSON;
			}
		}
		
		return null;
	}
}
