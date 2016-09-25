package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;

public class MediaTypeLayout extends AbstractLayout {

	private final Map<MediaType, String> mediaTypeMap = new HashMap<>();
	private String defaultPath;
	
	public MediaTypeLayout() {
		configureDefaultLayout();
	}

	public void setPathFor(MediaType mediaType, String path) {
		mediaTypeMap.put(mediaType, path);
	}
	
	public void setDefaultPath(String path) {
		this.defaultPath = path;
	}
	
	@Override
	protected String doMapPath(Locator location) {
		return null;
	}
	
	protected void configureDefaultLayout() {
		setDefaultPath("/");

		String imagePath = "/images/";
		setPathFor(MediaType.IMAGE_PNG, imagePath);
		setPathFor(MediaType.IMAGE_JPEG, imagePath);
		setPathFor(MediaType.IMAGE_GIF, imagePath);
		setPathFor(MediaType.IMAGE_SVG_XML, imagePath);
	
		String stylePath = "/styles/";
		setPathFor(MediaType.TEXT_CSS, stylePath);
		
		String scriptPath = "/scripts/";
		setPathFor(MediaType.APPLICATION_JAVASCRIPT, scriptPath);
	}
}
