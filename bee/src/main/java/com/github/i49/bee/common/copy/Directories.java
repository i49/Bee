package com.github.i49.bee.common.copy;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Directories {

	/**
	 * Deletes specified directory and its content recursively.
	 * @param path directory to delete
	 * @throws IOException
	 */
	public static void remove(Path path) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException();
		}
		Path absolute = path.toAbsolutePath();
		if (!Files.exists(absolute) || !Files.isDirectory(absolute)) {
			return;
		}
		Files.walkFileTree(absolute, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);	
				return FileVisitResult.CONTINUE;
			}
			
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}
}
