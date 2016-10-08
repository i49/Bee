package com.github.i49.bee.hives.layouts;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;

public interface Layout {

	String mapPath(Locator rmeotePath, MediaType mediaType) throws LayoutException;
	
	String getMappedPath(Locator remotePath);
	
	void setIndexName(String name);
	
	DirectoryConfiguration getDirectoryConfiguration(String path);
}
