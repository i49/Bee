package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.function.Predicate;

public interface Storage {
	
	void open(Path path, boolean clean) throws HiveException;
	
	void close() throws IOException;

	boolean isDirectory();
	
	void addItem(String path, byte[] content, FileTime lastModified) throws HiveException;
	
	void traverseForUpdate(Predicate<String> predicate) throws HiveException;
}
