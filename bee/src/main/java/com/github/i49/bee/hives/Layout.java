package com.github.i49.bee.hives;

import com.github.i49.bee.web.Locator;

public interface Layout {

	void setIndexName(String name);
	
	boolean find(Locator remotePath);

	String mapPath(Locator rmeotePath);
}
