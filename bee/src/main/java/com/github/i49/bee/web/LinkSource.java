package com.github.i49.bee.web;

import java.net.URI;
import java.util.Map;

public interface LinkSource {

	void rewriteLinks(Map<URI, URI> map);
}
