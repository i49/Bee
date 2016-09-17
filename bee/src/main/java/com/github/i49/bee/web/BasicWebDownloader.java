package com.github.i49.bee.web;

import java.io.ByteArrayOutputStream;
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

public class BasicWebDownloader implements WebDownloader {

	private static final Log log = LogFactory.getLog(BasicWebDownloader.class);

	private static final String DEFAULT_ENCODING = "UTF-8";
	
	private final CloseableHttpClient httpClient; 
	
	public BasicWebDownloader() {
		this.httpClient = HttpClients.createDefault();
	}
	
	@Override
	public void close() {
		try {
			this.httpClient.close();
			log.debug("HTTP client was closed gracefully.");
		} catch (IOException e) {
			// Ignores exception
			log.debug("Failed to close HTTP client: " + e.getMessage());
		}
	}

	@Override
	public WebResource download(Locator location) throws WebException {
		HttpGet request = new HttpGet(location.toURI());
		HttpClientContext context = new HttpClientContext();
		try (CloseableHttpResponse response = this.httpClient.execute(request, context)) {
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return createResource(location, context, response);
			} else {
				throw new HttpStatusException(location, statusCode);
			}
		} catch (IOException e) {
			throw new TransferException(location, e);
		}
	}
	
	protected WebResource createResource(Locator initialLocation, HttpClientContext context, HttpResponse response) throws WebException {
		ResourceMetadata metadata = createMetadata(initialLocation, context, response);
		final HttpEntity entity = response.getEntity();
		byte[] content = null;
		try (InputStream stream = entity.getContent()) {
			content = readContent(stream, metadata.getContentLength());
		} catch (IOException e) {
			throw new TransferException(metadata.getLocation(), e);
		}
		return createResource(initialLocation, metadata, content);
	}
	
	protected ResourceMetadata createMetadata(Locator initialLocation, HttpClientContext context, HttpResponse response) throws UnsupportedMediaException {
		final HttpEntity entity = response.getEntity();
		ResourceMetadata.Builder builder = ResourceMetadata.builder();
		builder.setLocation(getFinalLocation(initialLocation, context));
		builder.setMediaType(parseMediaType(entity));
		final String lastModified = response.getLastHeader("Last-Modified").getValue();
		if (lastModified != null) {
			builder.setLastModified(DateUtils.parseDate(lastModified));
		}
		if (entity.getContentEncoding() != null) {
			builder.setContentEncoding(entity.getContentEncoding().getValue());
		}
		builder.setContentLength(entity.getContentLength());
		return builder.build();
	}
	
	protected WebResource createResource(Locator initialLocation, ResourceMetadata metadata, byte[] content) throws WebException {
		final MediaType mediaType = metadata.getMediaType();
		if (mediaType == MediaType.TEXT_HTML || mediaType == MediaType.APPLICATION_XHTML_XML) {
			return HtmlWebResource.create(metadata, content, DEFAULT_ENCODING);
		} else {
			return BinaryWebResource.create(metadata, content);
		}
	}
	
	private static byte[] readContent(InputStream in, long length) throws IOException {
		ByteArrayOutputStream out = (length >= 0) ? new ByteArrayOutputStream((int)length) : new ByteArrayOutputStream();
		byte[] buffer = new byte[64 * 1024];
		for (;;) {
			int len = in.read(buffer);
			if (len < 0) {
				break;
			}
			out.write(buffer, 0, len);
		}
		return out.toByteArray();
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
			throw new UnsupportedMediaException(tokens[0]);
		}
		return mediaType;
	}
}
