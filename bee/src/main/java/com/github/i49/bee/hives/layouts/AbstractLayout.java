package com.github.i49.bee.hives.layouts;

import java.util.HashMap;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.MediaType;

public abstract class AbstractLayout implements Layout {

	private static final String DEFAULT_INDEX_NAME = "index.html";

	private String indexName = DEFAULT_INDEX_NAME;
	
	private final Map<Locator, String> mapped = new HashMap<>();
	private final Cell root = Cell.rootCell();
	
	private final ExtensionMapper extensionMapper = new ExtensionMapper();

	private final Map<String, DirectoryConfiguration> directoryConfigurations = new HashMap<>();
	
	protected AbstractLayout() {
	}
	
	@Override
	public String mapPath(Locator remotePath, MediaType mediaType) throws LayoutException {
		if (remotePath == null) {
			throw new IllegalArgumentException("remotePath is null");
		}
		if (mediaType == null) {
			throw new IllegalArgumentException("mediaType is null");
		}
		String localPath = getMappedPath(remotePath);
		if (localPath == null) {
			localPath = mapNewPath(remotePath, mediaType);
			if (localPath != null) {
				this.mapped.put(remotePath, localPath);
			}
		}
		return localPath;
	}

	@Override
	public String getMappedPath(Locator remotePath) {
		if (remotePath == null) {
			throw new IllegalArgumentException("remotePath is null");
		}
		return this.mapped.get(remotePath);
	}
	
	@Override
	public void setIndexName(String name) {
		this.indexName = name;
	}
	
	@Override
	public DirectoryConfiguration getDirectoryConfiguration(String path) {
		DirectoryConfiguration c = this.directoryConfigurations.get(path);
		if (c == null) {
			c = new DirectoryConfigurationImpl(path);
			this.directoryConfigurations.put(path, c);
		}
		return c;
	}
	
	protected String mapNewPath(Locator remotePath, MediaType mediaType) throws LayoutException {
		String localPath = doMapPath(remotePath, mediaType);
		if (localPath == null) {
			return null;
		}
		localPath = modifyPath(localPath, mediaType);
		this.root.addHoney(localPath);
		return localPath;
	}

	protected String modifyPath(String localPath, MediaType mediaType) {
		if (localPath.endsWith("/")) {
			localPath += this.indexName;
		}
		return modifyExtension(localPath, mediaType);
	}
	
	protected String modifyExtension(String localPath, MediaType mediaType) {
		return this.extensionMapper.mapExtension(localPath, mediaType);
	}
	
	protected Cell getCell(String localPath) throws NotCellException {
		return this.root.getCell(localPath);
	}
	
	abstract protected String doMapPath(Locator location, MediaType mediaTYpe) throws LayoutException;

	private static class DirectoryConfigurationImpl implements DirectoryConfiguration {

		private final String path;
		private Map<String, Object> properties = new HashMap<>();
		
		public DirectoryConfigurationImpl(String path) {
			this.path = path;
		}
		
		public String getPath() {
			return path;
		}
		
		public Map<String, Object> getProperties() {
			return properties;
		}
	}
}
