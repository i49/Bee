package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public interface Storage {
	
	void open(Path path, boolean clean) throws IOException;
	
	void close() throws IOException;

	boolean isDirectory();
	
	void saveAt(String path, byte[] content, FileTime lastModified) throws IOException;
}
