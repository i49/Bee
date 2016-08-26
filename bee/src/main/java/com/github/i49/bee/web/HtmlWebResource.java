package com.github.i49.bee.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class HtmlWebResource extends AbstractWebResource implements LinkSource {

	private static final Log log = LogFactory.getLog(HtmlWebResource.class);

	private final Document document;

	protected HtmlWebResource(Locator location, Document document) {
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
	
	@Override
	public Collection<Link> getComponentLinks() {
		LinkedHashSet<Link> links = new LinkedHashSet<>();
		for (Element e : findElementsByName("link")) {
			if ("stylesheet".equals(e.getAttribute("rel"))) {
				final MediaType type = parseType(e.getAttribute("type"), MediaType.TEXT_CSS);
				Locator location = parseLink(e.getAttribute("href"));
				if (location != null) {
					links.add(new Link(location, type));
				}
			}
		}
		for (Element e : findElementsByName("script")) {
			final MediaType type = parseType(e.getAttribute("type"), MediaType.TEXT_JAVASCRIPT);
			if (type == MediaType.TEXT_JAVASCRIPT || type == MediaType.APPLICATION_JAVASCRIPT) {
				Locator location = parseLink(e.getAttribute("src"));
				if (location != null) {
					links.add(new Link(location, type));
				}
			}
		}
		for (Element e : findElementsByName("img")) {
			Locator location = parseLink(e.getAttribute("src"));
			if (location != null) {
				links.add(new Link(location, null));
			}
		}
		return links;
	}

	@Override
	public Collection<Link> getExternalLinks() {
		LinkedHashSet<Link> links = new LinkedHashSet<>();
		for (Element e : findElementsByName("a")) {
			Locator location = parseLink(e.getAttribute("href"));
			if (location != null) {
				links.add(new Link(location, null));
			}
		}
		return links;
	}

	@Override
	public void rewriteLinks(Map<Locator, Locator> map) {
		rewriteLinks("link", "href", map);
		rewriteLinks("script", "src", map);
		rewriteLinks("img", "src", map);
		rewriteLinks("a", "href", map);
	}

	protected Iterable<Element> findElementsByName(String name) {
		List<Element> result = new ArrayList<>();
		NodeList nodes = this.document.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			result.add((Element)nodes.item(i));
		}
		return result;
	}

	protected Locator parseLink(String value) {
		if (value == null) {
			return null;
		}
		String[] parts = value.split("#");
		if (parts.length >= 1) {
			return resolve(parts[0]);
		} else {
			return null;
		}
	}
	
	protected MediaType parseType(String value, MediaType defaultType) {
		return (value != null) ? MediaType.of(value) : defaultType;
	}
	
	protected Locator resolve(String value) {
		value = value.trim();
		if (value.isEmpty() || value.equals("#")) {
			return getFinalLocation();
		}
		return getFinalLocation().resolve(value);
	}

	protected void rewriteLinks(String element, String attribute, Map<Locator, Locator> map) {
		for (Element e : findElementsByName(element)) {
			String oldValue = e.getAttribute(attribute);
			if (oldValue != null) {
				String newValue = convertLink(oldValue, map);
				if (newValue != null) {
					e.setAttribute(attribute, newValue);
				}
			}
		}
	}
		
	protected String convertLink(String value, Map<Locator, Locator> map) {
		String[] parts = value.split("#");
		if (parts.length == 0) {
			return null;
		}
		parts[0] = parts[0].trim();
		if (parts[0].isEmpty()) {
			return null;
		}
		Locator oldTarget = resolve(parts[0]);
		if (oldTarget == null) {
			return null;
		}
		Locator newTarget = map.get(oldTarget);
		if (newTarget == null) {
			return null;
		}
		String newValue = newTarget.toString();
		if (parts.length > 1) {
			newValue = String.join("#", newValue, parts[1]);
		}
		return newValue;
	}
	
	public static HtmlWebResource create(Locator location, InputStream stream, String encoding) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		InputSource source = new InputSource(stream);
		source.setEncoding(encoding);
		Document document = builder.parse(source);
		return new HtmlWebResource(location, document);
	}
}
