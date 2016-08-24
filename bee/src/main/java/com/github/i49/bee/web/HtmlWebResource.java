package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class HtmlWebResource extends AbstractWebResource {

	private static final Log log = LogFactory.getLog(HtmlWebResource.class);

	private final Document document;

	protected HtmlWebResource(URI location, Document document) {
		super(location, MediaType.APPLICATION_XHTML_XML);
		this.document = document;
	}
	
	public Document getDocument() {
		return document;
	}

	@Override
	public byte[] getContent(ResourceSerializer serializer) {
		return serializer.writeHtmlDocument(getDocument());
	}

	public Collection<URI> getLinkedPages() {
		LinkedHashSet<URI> result = new LinkedHashSet<>();
		for (URI link : getOutboundLinks()) {
			URI page = withoutFragment(link);
			if (page != null && !page.equals(getLocation())) {
				result.add(page);
			}
		}
		return result;
	}
	
	public Collection<URI> getOutboundLinks() {
		List<URI> result = new ArrayList<>();
		for (Element e : findElementsByName("a")) {
			String value = e.getAttribute("href");
			URI link = resolve(correctLink(value));
			if (link != null && !link.isOpaque()) {
				result.add(link);
			}
		}
		return result;
	}
	
	public Collection<URI> getImageLinks() {
		LinkedHashSet<URI> result = new LinkedHashSet<>();
		for (Element e : findElementsByName("img")) {
			String value = e.getAttribute("src");
			URI link = resolve(value);
			if (link != null && !link.isOpaque()) {
				result.add(link);
			}
		}
		return result;
	}
	
	private Iterable<Element> findElementsByName(String name) {
		List<Element> result = new ArrayList<>();
		NodeList nodes = this.document.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			result.add((Element)nodes.item(i));
		}
		return result;
	}
	
	private static String correctLink(String value) {
		String[] parts = value.split("#");
		if (parts.length < 2) {
			return value;
		}
		String fragment = parts[1].replaceAll(" ", "%20");
		return String.join("#", parts[0], fragment);
	}

	private URI resolve(String value) {
		value = value.trim();
		if (value.isEmpty() || value.equals("#")) {
			return getFinalLocation();
		}
		try {
			return getFinalLocation().resolve(value);
		} catch (IllegalArgumentException e) {
			log.debug("Failed to resolve " + value + " on " + getLocation().toString());
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
	
	public static HtmlWebResource contentOf(URI location, InputStream stream, String encoding) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		InputSource source = new InputSource(stream);
		source.setEncoding(encoding);
		Document document = builder.parse(source);
		return new HtmlWebResource(location, document);
	}
}
