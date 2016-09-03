package com.github.i49.bee.web;

public interface WebDownloader extends AutoCloseable {

	WebResource download(Locator location) throws Exception;
}
