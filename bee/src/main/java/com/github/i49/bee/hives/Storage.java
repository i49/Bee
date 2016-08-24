package com.github.i49.bee.hives;

import java.io.IOException;

public interface Storage {
	
	void open(String path) throws IOException;
	
	void close() throws IOException;

	void saveAt(String path, byte[] content) throws IOException;
}
