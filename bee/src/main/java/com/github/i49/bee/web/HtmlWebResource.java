package com.github.i49.bee.web;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class HtmlWebResource extends AbstractWebResource implements LinkSourceResource {

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
	public Collection<Link> getLinks() {
		List<Link> links = new ArrayList<>();
		collectLinks("link", "href", links);
		collectLinks("script", "src", links);
		collectLinks("img", "src", links);
		collectLinks("a", "href", links);
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
	
	protected void processElement(String name, Consumer<Element> action) {
		NodeList nodes = this.document.getElementsByTagName(name);
		for (int i = 0; i < nodes.getLength(); i++) {
			action.accept((Element)nodes.item(i));
		}
	}

	protected void collectLinks(String element, String attribute, Collection<Link> result) {
		processElement(element, e->{
			Locator location = parseLink(e.getAttribute(attribute));
			if (location == null) {
				return;
			}
			Set<LinkType> linkTypes = parseRelation(e.getAttribute("rel"));
			MediaType mediaType = MediaType.of(e.getAttribute("type"));
			Link link = Link.create(location, element, linkTypes, mediaType);
			if (link != null) {
				result.add(link);
			}
		});
	}
	
	protected Locator parseLink(String value) {
		if (value == null) {
			return null;
		}
		String[] parts = value.split("#");
		if (parts.length == 0) {
			return null;	
		}
		return resolve(parts[0]);
	}
	
	protected Set<LinkType> parseRelation(String rel) {
		if (rel == null || rel.isEmpty()) {
			return null;
		}
		Set<LinkType> types = new LinkedHashSet<>();
		for (String value : rel.split("\\s+")) {
			LinkType type = LinkType.of(value);
			if (type != null) {
				types.add(type);
			}
		}
		return types;
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
	
	public static HtmlWebResource create(ResourceMetadata metadata, byte[] content, String defaultEncoding) throws WebContentException {
		HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
		InputSource source = new InputSource(new ByteArrayInputStream(content));
		String encoding = metadata.getContentEncoding();
		source.setEncoding((encoding != null) ? encoding : defaultEncoding);
		try {
			Document document = builder.parse(source);
			return new HtmlWebResource(metadata, document);
		} catch (Exception e) {
			throw new WebContentException(metadata.getLocation(), e);
		}
	}

	protected HtmlWebResource(ResourceMetadata metadata, Document document) {
		super(metadata);
		this.document = document;
	}
}
