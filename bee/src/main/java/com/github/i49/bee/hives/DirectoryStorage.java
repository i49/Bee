package com.github.i49.bee.hives;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DirectoryStorage implements Storage {
	
	private static final Log log = LogFactory.getLog(DirectoryStorage.class);

	private Path root;
	
	public DirectoryStorage() {
	}
	
	@Override
	public void open(Path path) throws IOException {
		this.root = path;
		Files.createDirectories(this.root);
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void saveAt(String path, byte[] content, FileTime lastModified) throws IOException {
		Path fullpath = this.root.resolve(path.substring(1)).toAbsolutePath();
		Files.createDirectories(fullpath.getParent());
		try (OutputStream stream = Files.newOutputStream(fullpath)) {
			if (content != null) {
				stream.write(content);
			}
		} catch (IOException e) {
			throw e;
		}
		Files.setLastModifiedTime(fullpath, lastModified);
	}
}
