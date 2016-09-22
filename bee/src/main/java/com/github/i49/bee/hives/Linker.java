package com.github.i49.bee.hives;

import java.io.IOException;

import com.github.i49.bee.web.ResourceMetadata;
import com.github.i49.bee.web.WebContentException;

public interface Linker {

	void link(String localPath, ResourceMetadata metadata) throws IOException, WebContentException;
}
