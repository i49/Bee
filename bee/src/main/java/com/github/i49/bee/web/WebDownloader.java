package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.DateUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class WebDownloader implements AutoCloseable {

	private static final Log log = LogFactory.getLog(WebDownloader.class);

	private static final String DEFAULT_ENCODING = "UTF-8";
	
	private final CloseableHttpClient httpClient; 
	
	public WebDownloader() {
		this.httpClient = HttpClients.createDefault();
	}
	
	public WebResource download(Locator location) throws Exception {
		HttpGet request = new HttpGet(location.toURI());
		HttpClientContext context = new HttpClientContext();
		try (CloseableHttpResponse response = this.httpClient.execute(request, context)) {
			final int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				return createResource(location, context, response);
			} else {
				throw new IOException("Failed to get " + location.toString() + " (" + code + ")");
			}
		}
	}
	
	protected ResourceMetadata createMetadata(Locator initialLocation, HttpClientContext context, HttpResponse response) throws UnsupportedMediaException {
		final HttpEntity entity = response.getEntity();
		ResourceMetadata.Builder builder = ResourceMetadata.builder();
		builder.setLocation(getFinalLocation(initialLocation, context));
		builder.setMediaType(parseMediaType(entity));
		String lastModified = response.getLastHeader("Last-Modified").getValue();
		if (lastModified != null) {
			builder.setLastModified(DateUtils.parseDate(lastModified));
		}
		return builder.build();
	}
	
	private WebResource createResource(Locator initialLocation, HttpClientContext context, HttpResponse response) throws Exception {
		ResourceMetadata metadata = createMetadata(initialLocation, context, response);
		final MediaType mediaType = metadata.getMediaType();
		final HttpEntity entity = response.getEntity();
		if (mediaType == MediaType.TEXT_HTML || mediaType == MediaType.APPLICATION_XHTML_XML) {
			try (InputStream stream = entity.getContent()) {
				return HtmlWebResource.create(metadata, stream, DEFAULT_ENCODING);
			}
		} else {
			byte[] content = EntityUtils.toByteArray(entity);
			return BinaryWebResource.create(metadata, content);
		}
	}
	
	private static Locator getFinalLocation(Locator initialLocation, HttpClientContext context) {
		Locator redirectLocation = getRedirectLocation(context);
		return (redirectLocation != null) ? redirectLocation : initialLocation;
	}

	private static Locator getRedirectLocation(HttpClientContext context) {
		List<URI> locations = context.getRedirectLocations();
		if (locations == null || locations.isEmpty()) {
			return null;
		} else {
			return Locator.fromURI(locations.get(locations.size() - 1));
		}
	}
	
	private static MediaType parseMediaType(HttpEntity entity) throws UnsupportedMediaException {
		final String contentType = entity.getContentType().getValue();
		String[] tokens = contentType.split(";");
		MediaType mediaType = MediaType.of(tokens[0]);
		if (mediaType == null) {
			throw new UnsupportedMediaException("Unsupported media type: " + tokens[0]);
		}
		return mediaType;
	}
	
	@Override
	public void close() throws IOException {
		this.httpClient.close();
		log.debug("HTTP client was gracefully closed.");
	}
}
