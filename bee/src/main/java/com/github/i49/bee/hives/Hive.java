package com.github.i49.bee.hives;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import com.github.i49.bee.web.Locator;
import com.github.i49.bee.web.WebResource;

public interface Hive extends AutoCloseable {
	
	Path getBasePath();
	
	void setBasePath(Path basePath);

	Storage getStorage();
	
	void setStorage(Storage storage);
	
	void open() throws IOException;
	
	String store(WebResource resource) throws HiveException;
	
	Linker createLinker(Map<Locator, Locator> redirections);
}
