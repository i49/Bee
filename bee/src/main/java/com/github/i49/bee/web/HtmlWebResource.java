package com.github.i49.bee.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

public class HtmlWebResource extends AbstractWebResource implements LinkProvidingResource {

	private static final Log log = LogFactory.getLog(HtmlWebResource.class);

	private final Document document;

	public Document getDocument() {
		return document;
	}
	
	@Override
	public byte[] getBytes(ResourceSerializer serializer) {
		return serializer.writeHtmlDocument(getDocument());
	}
	
	@Override
	public Collection<Link> getDependencyLinks() {
		LinkedHashSet<Link> links = new LinkedHashSet<>();
		collectStylesheets(links);
		collectScripts(links);
		collectImages(links);
		return links;
	}

	@Override
	public Collection<Link> getExternalLinks() {
		LinkedHashSet<Link> links = new LinkedHashSet<>();
		for (Element e : findElementsByName("a")) {
			Link link = parseLink(e, "href", null);
			if (link != null) {
				links.add(link);
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

	protected void collectStylesheets(Collection<Link> links) {
		for (Element e : findElementsByName("link")) {
			if ("stylesheet".equals(e.getAttribute("rel"))) {
				Link link = parseLink(e, "href", MediaType.TEXT_CSS);
				if (link != null) {
					links.add(link);
				}
			}
		}
	}
	
	protected void collectScripts(Collection<Link> links) {
		for (Element e : findElementsByName("script")) {
			Link link = parseLink(e, "src", MediaType.TEXT_JAVASCRIPT);
			if (link != null) {
				MediaType type = link.getMediaType();
				if (type == MediaType.TEXT_JAVASCRIPT || type == MediaType.APPLICATION_JAVASCRIPT) {
					links.add(link);	
				}
			}
		}
	}
	
	protected void collectImages(Collection<Link> links) {
		for (Element e : findElementsByName("img")) {
			Link link = parseLink(e, "src", null);
			if (link != null) {
				links.add(link);
			}
		}
	}
	
	protected Link parseLink(Element e, String attributeName, MediaType defaultType) {
		MediaType type = parseLinkType(e, defaultType);
		String value = e.getAttribute(attributeName);
		if (value == null) {
			return null;
		}
		String[] parts = value.split("#");
		if (parts.length >= 1) {
			return Link.create(resolve(parts[0]), type);
		} else {
			return null;
		}
	}
	
	protected MediaType parseLinkType(Element e, MediaType defaultType) {
		final String value = e.getAttribute("type");
		return (value != null) ? MediaType.of(value) : defaultType;
	}

	protected Locator resolve(String value) {
		value = value.trim();
		Locator base = getMetadata().getLocation();
		if (value.isEmpty() || value.equals("#")) {
			return base;
		}
		return base.resolve(value);
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
	
	public static HtmlWebResource create(ResourceMetadata metadata, byte[] content, String defaultEncoding) throws SAXException, IOException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		InputSource source = new InputSource(new ByteArrayInputStream(content));
		String encoding = metadata.getContentEncoding();
		source.setEncoding((encoding != null) ? encoding : defaultEncoding);
		Document document = builder.parse(source);
		return new HtmlWebResource(metadata, document);
	}

	protected HtmlWebResource(ResourceMetadata metadata, Document document) {
		super(metadata);
		this.document = document;
	}
}
