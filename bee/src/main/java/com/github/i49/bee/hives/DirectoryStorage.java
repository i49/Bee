
package com.github.i49.bee.hives;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.AccessMode;
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
	public void open(Path path, boolean clean) throws IOException {
		this.root = path;
		if (clean) {
			cleanStorage();
		}
		createStorage();
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public FileTime getLastModifiedTime(String path) throws IOException {
		return Files.getLastModifiedTime(resolve(path));
	}

	@Override
	public byte[] read(String path) throws IOException {
		try {
			return Files.readAllBytes(resolve(path));
		} catch (IOException e) {
			throw new StorageIOException(path, AccessMode.READ, e);
		}
	}
	
	@Override
	public void write(String path, byte[] content, FileTime lastModified) throws IOException {
		Path fullpath = resolve(path);
		try {
			Files.createDirectories(fullpath.getParent());
			try (OutputStream stream = Files.newOutputStream(fullpath)) {
				if (content != null) {
					stream.write(content);
				}
			}
			Files.setLastModifiedTime(fullpath, lastModified);
		} catch (IOException e) {
			throw new StorageIOException(path, AccessMode.WRITE, e);
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
	
	protected Path resolve(String path) {
		if (!path.startsWith("/")) {
			throw new IllegalArgumentException("Invalid path: " + path);
		}
		return this.root.resolve(path.substring(1)).toAbsolutePath();
	}
}
