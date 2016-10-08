package com.github.i49.bee.hives.layouts;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;

public class MediaTypeLayout extends AbstractLayout {

	private final Map<MediaType, String> paths = new HashMap<>();
	private String defaultPath;
	
	private DecimalFormat entryNumberFormat;
	
	public MediaTypeLayout() {
		configureDefaultLayout();
		this.entryNumberFormat = new DecimalFormat("0000");
	}

	public void setPathFor(MediaType mediaType, String path) {
		paths.put(mediaType, path);
	}
	
	public void setDefaultPath(String path) {
		this.defaultPath = path;
	}
	
	@Override
	protected String doMapPath(Locator location, MediaType mediaType) throws LayoutException {
		String localPath = this.paths.get(mediaType);
		if (localPath == null) {
			localPath = this.defaultPath;
		}
		
		Cell cell = getCell(localPath);

		StringBuilder b = new StringBuilder(localPath);
		int entryNumber = cell.countHoneys() + 1;
		String entryNumberPart = this.entryNumberFormat.format(entryNumber);
		b.append(entryNumberPart);
		return b.toString();
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
