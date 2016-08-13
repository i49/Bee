package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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

	private final URL location;
	private final Document document;
	
	public HtmlWebResource(URL location, Document document) {
		this.location = location;
		this.document = document;
	}
	
	@Override
	public URL getLocation() {
		return location;
	}
	
	@Override
	public String getContentType() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Document getDocument() {
		return document;
	}
	
	public URI getURI() {
		try {
			return location.toURI();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<URL> getOutboundLinks() {
		List<URL> links = new ArrayList<>();
		NodeList nodes = this.document.getElementsByTagName("a");
		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element)nodes.item(i);
			String href = element.getAttribute("href");
			if (!href.isEmpty() && !href.startsWith("#")) {
				try {
					URL target = new URL(getLocation(), href);
					links.add(target);
				} catch (MalformedURLException e) {
					log.warn("Invalid href value: " + href);
				}
			}
		}
		return links;
	}
	
	public static HtmlWebResource contentOf(URL location, InputStream stream) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Document document = builder.parse(stream);
		return new HtmlWebResource(location, document);
	}
}
