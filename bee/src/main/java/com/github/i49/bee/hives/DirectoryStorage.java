package com.github.i49.bee.hives;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.github.i49.bee.common.Directories;

public class DirectoryStorage implements Storage {
	
	private static final Log log = LogFactory.getLog(DirectoryStorage.class);

	private Path root;
	
	public DirectoryStorage() {
	}
	
	@Override
	public void open(Path path, boolean clean) throws HiveException {
		this.root = path;
		try {
			if (clean) {
				cleanStorage();
			}
			createStorage();
		} catch (IOException e) {
			throw new StorageCreationException(this.root, e);
		}
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean isDirectory() {
		return true;
	}
	
	@Override
	public void saveAt(String path, byte[] content, FileTime lastModified) throws HiveException {
		Path fullpath = this.root.resolve(path.substring(1)).toAbsolutePath();
		try {
			Files.createDirectories(fullpath.getParent());
			try (OutputStream stream = Files.newOutputStream(fullpath)) {
				if (content != null) {
					stream.write(content);
				}
			}
			Files.setLastModifiedTime(fullpath, lastModified);
		} catch (IOException e) {
			throw new ContentWriteException(fullpath, e);
		}
	}
	
	protected void cleanStorage() throws IOException {
		if (Files.exists(this.root)) {
			log.debug("Cleaning output directory: " + this.root);
			Directories.remove(this.root);
		}
	}
	
	protected void createStorage() throws IOException {
		log.debug("Creating output directory: " + this.root);
		Files.createDirectories(this.root);
	}
}
