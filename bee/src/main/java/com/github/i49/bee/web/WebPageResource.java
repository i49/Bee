package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class WebPageResource implements WebResource {

	private final URL location;
	private final Document document;
	
	public WebPageResource(URL location, Document document) {
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
	
	public static WebPageResource contentOf(URL location, InputStream stream) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		Document document = builder.parse(stream);
		return new WebPageResource(location, document);
	}
}
