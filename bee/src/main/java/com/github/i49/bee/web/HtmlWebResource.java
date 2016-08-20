package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class HtmlWebResource implements WebResource {

	private static final Log log = LogFactory.getLog(HtmlWebResource.class);

	private final URI location;
	private final Document document;
	
	protected HtmlWebResource(URI location, Document document) {
		this.location = location;
		this.document = document;
	}
	
	@Override
	public URI getLocation() {
		return location;
	}
	
	@Override
	public MediaType getMediaType() {
		return MediaType.APPLICATION_XHTML_XML;
	}
	
	public Document getDocument() {
		return document;
	}

	public Iterable<URI> getLinkedPages() {
		LinkedHashSet<URI> result = new LinkedHashSet<>();
		for (URI link : getOutboundLinks()) {
			URI page = withoutFragment(link);
			if (page != null && !page.equals(getLocation())) {
				result.add(page);
			}
		}
		return result;
	}
	
	public List<URI> getOutboundLinks() {
		List<URI> result = new ArrayList<>();
		for (Element e : findElementsByName("a")) {
			String value = e.getAttribute("href");
			URI link = resolve(value);
			if (link != null && !link.isOpaque()) {
				result.add(link);
			}
		}
		return result;
	}
	
	private List<Element> findElementsByName(String name) {
		List<Element> result = new ArrayList<>();
		NodeList nodes = this.document.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			result.add((Element)nodes.item(i));
		}
		return result;
	}

	private URI resolve(String value) {
		value = value.trim();
		if (value.isEmpty() || value.equals("#")) {
			return getLocation();
		}
		try {
			return getLocation().resolve(value);
		} catch (IllegalArgumentException e) {
			log.debug("Failed to resolve " + value + " by " + getLocation().toString());
			return null;
		}
	}
	
	private static URI withoutFragment(URI location) {
		if (location.getFragment() == null) {
			return location;
		}
		try {
			return new URI(location.getScheme(), location.getSchemeSpecificPart(), null);
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static HtmlWebResource contentOf(URI location, InputStream stream) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Document document = builder.parse(stream);
		return new HtmlWebResource(location, document);
	}
}
