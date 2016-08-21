package com.github.i49.bee.hives;

import com.github.i49.bee.web.WebResource;

public interface Storage {

	void saveResourceAt(String path, WebResource resource);
}
