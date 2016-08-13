package com.github.i49.bee.web;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class WebDownloader {

	private static final Log log = LogFactory.getLog(WebDownloader.class);
	
	public WebDownloader() {
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
	}
	
	public WebResource download(URL location) throws IOException {
		HttpURLConnection conn = (HttpURLConnection)location.openConnection();
		conn.connect();
		int code = conn.getResponseCode();
		if (code != HttpURLConnection.HTTP_OK) {
			log.warn("HTTP status code = " + code);
		}
		return null;
	}
}
