package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.web.WebResource;

public class DirectoryStorage implements Storage {
	
	private static final Log log = LogFactory.getLog(DirectoryStorage.class);

	private final FileSystem fs = FileSystems.getDefault();
	private Path root;
	
	public DirectoryStorage() {
	}
	
	@Override
	public void open(String path) throws IOException {
		this.root = this.fs.getPath(path);
		Files.createDirectories(this.root);
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void saveResourceAt(String path, WebResource resource) throws IOException {
		Path fullpath = this.root.resolve(path.substring(1)).toAbsolutePath();
		log.debug("Stored at " + fullpath.toString());
		Files.createDirectories(fullpath.getParent());
	}
}
