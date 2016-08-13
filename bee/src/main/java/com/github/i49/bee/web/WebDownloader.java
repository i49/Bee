package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class WebDownloader {

	private static final Log log = LogFactory.getLog(WebDownloader.class);
	
	public WebDownloader() {
		CookieManager manager = new CookieManager();
		CookieHandler.setDefault(manager);
	}
	
	public WebResource download(URL location) throws IOException, SAXException {
		HttpURLConnection conn = (HttpURLConnection)location.openConnection();
		conn.connect();
		int code = conn.getResponseCode();
		log.debug("HTTP status code = " + code);
		if (code != HttpURLConnection.HTTP_OK) {
			return null;
		}
		String contentType = conn.getContentType();
		log.debug("Content type = " + contentType);
		try (InputStream stream = conn.getInputStream()) {
			return HtmlWebResource.contentOf(location, stream);
		}
	}
}
