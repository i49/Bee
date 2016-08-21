package com.github.i49.bee.hives;

import com.github.i49.bee.web.WebResource;

public interface Hive {
	
	void setBasePath(String basePath);
	
	void open();
	
	void close();

	void store(WebResource resource);
}
