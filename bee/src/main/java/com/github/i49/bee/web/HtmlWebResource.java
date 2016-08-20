package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	public List<URI> getOutboundLinks() {
		List<URI> links = new ArrayList<>();
		Set<URI> found = new HashSet<>();
		NodeList nodes = this.document.getElementsByTagName("a");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element)nodes.item(i);
			String value = element.getAttribute("href");
			if (!value.isEmpty() && !value.startsWith("#")) {
				URI target = null;
				try {
					target = new URI(value);
				} catch (URISyntaxException e) {
					log.info("Invalid link target: " + value);
					continue;
				}
				if (target.isOpaque()) {
					continue;
				}
				URI absolute = getLocation().resolve(target);
				if (found.contains(absolute)) {
					continue;
				}
				links.add(absolute);
				found.add(absolute);
			}
		}
		return links;
	}
	
	public static HtmlWebResource contentOf(URI location, InputStream stream) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Document document = builder.parse(stream);
		return new HtmlWebResource(location, document);
	}
}
