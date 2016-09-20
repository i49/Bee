package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

public interface Storage {
	
	void open(Path path, boolean clean) throws IOException;
	
	void close() throws IOException;

	boolean isDirectory();
	
	FileTime getLastModifiedTime(String path) throws IOException;

	byte[] read(String path) throws IOException;
	
	void write(String path, byte[] content, FileTime lastModified) throws IOException;
}
