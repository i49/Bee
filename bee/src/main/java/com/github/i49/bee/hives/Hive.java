package com.github.i49.bee.hives;

import java.io.IOException;
import java.util.List;

import com.github.i49.bee.web.Link;
import com.github.i49.bee.web.WebResource;

public interface Hive {
	
	void setBasePath(String basePath);
	
	void open() throws IOException;
	
	void close() throws IOException;

	void store(WebResource resource, List<Link> links) throws IOException;
}
