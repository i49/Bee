package com.github.i49.bee.hives;

import java.io.IOException;

import com.github.i49.bee.web.WebResource;

public interface Storage {
	
	void open(String path) throws IOException;
	
	void close() throws IOException;

	void saveResourceAt(String path, WebResource resource) throws IOException;
}
