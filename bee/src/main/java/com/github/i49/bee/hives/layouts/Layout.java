package com.github.i49.bee.hives.layouts;

import com.github.i49.bee.web.Locator;

public interface Layout {

	void setIndexName(String name);
	
	boolean find(Locator remotePath);

	String mapPath(Locator rmeotePath);
	
	DirectoryConfiguration getDirectoryConfiguration(String path);
}
