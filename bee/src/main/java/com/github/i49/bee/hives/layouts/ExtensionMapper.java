package com.github.i49.bee.hives.layouts;

import java.util.EnumMap;
import java.util.Map;

import com.github.i49.bee.web.MediaType;

public class ExtensionMapper {

	private final EnumMap<MediaType, String> map = new EnumMap<>(MediaType.class);
	
	public ExtensionMapper() {
		setUpMap(this.map);
	}
	
	public Map<MediaType, String> getMap() {
		return map;
	}
	
	public String mapExtension(String localPath, MediaType mediaType) {
		String extension = this.map.get(mediaType);
		if (extension == null) {
			return localPath;
		}
		return removeExtension(localPath) + extension;
	}

	private static String removeExtension(String path) {
		int lastIndex = path.lastIndexOf('.');
		if (lastIndex >= 0) {
			return path.substring(0, lastIndex);
		} else {
			return path;
		}
	}
	
	protected void setUpMap(Map<MediaType, String> map) {
		map.put(MediaType.APPLICATION_JAVASCRIPT, ".js");
		map.put(MediaType.APPLICATION_JSON, ".json");
		map.put(MediaType.APPLICATION_RDF_XML, ".rdf");
		map.put(MediaType.APPLICATION_XHTML_XML, ".html");
		map.put(MediaType.APPLICATION_XML, ".xml");

		map.put(MediaType.IMAGE_GIF, ".gif");
		map.put(MediaType.IMAGE_JPEG, ".jpeg");
		map.put(MediaType.IMAGE_PNG, ".png");
		map.put(MediaType.IMAGE_SVG_XML, ".svg");
		
		map.put(MediaType.TEXT_CSS, ".css");
		map.put(MediaType.TEXT_HTML, ".html");
		map.put(MediaType.TEXT_JAVASCRIPT, ".js");
		map.put(MediaType.TEXT_PLAIN, ".txt");
		map.put(MediaType.TEXT_TURTLE, ".ttl");
	}
}
