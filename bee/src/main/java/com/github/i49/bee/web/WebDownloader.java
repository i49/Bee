package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.xml.sax.SAXException;

public class WebDownloader implements AutoCloseable {

	private static final Log log = LogFactory.getLog(WebDownloader.class);
	
	private final CloseableHttpClient httpClient; 
	
	public WebDownloader() {
		this.httpClient = HttpClients.createDefault();
	}
	
	public WebResource download(URI location) throws IOException, SAXException {
		HttpGet request = new HttpGet(location);
		HttpClientContext context = new HttpClientContext();
		try (CloseableHttpResponse response = this.httpClient.execute(request, context)) {
			final int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				URI finalLocation = getFinalLocation(location, context);
				return createWebResource(location, finalLocation, response.getEntity());
			} else {
				throw new IOException("Failed to get " + location.toString() + " (" + code + ")");
			}
		}
	}

	private static URI getFinalLocation(URI initialLocation, HttpClientContext context) {
		List<URI> locations = context.getRedirectLocations();
		if (locations == null || locations.isEmpty()) {
			return initialLocation;
		} else {
			return locations.get(locations.size() - 1);
		}
	}
	
	private WebResource createWebResource(URI initialLocation, URI finalLocation, HttpEntity entity) throws IOException, SAXException {
		String contentType = entity.getContentType().getValue();
		MediaType mediaType = parseMediaType(contentType);
		if (mediaType == null) {
			return null;
		}
		try (InputStream stream = entity.getContent()) {
			if (mediaType == MediaType.TEXT_HTML || mediaType == MediaType.APPLICATION_XHTML_XML) {
				return HtmlWebResource.contentOf(initialLocation, finalLocation, stream);
			} else {
				return BinaryWebResource.contentOf(initialLocation, finalLocation, mediaType, stream);
			}
		}
	}
	
	private static MediaType parseMediaType(String contentType) {
		String[] tokens = contentType.split(";");
		MediaType mediaType = MediaType.of(tokens[0]);
		if (mediaType == null) {
			log.error("Unknown media type: " + tokens[0]);
		}
		return mediaType;
	}

	@Override
	public void close() throws IOException {
		this.httpClient.close();
		log.debug("HTTP client was gracefully closed.");
	}
}
