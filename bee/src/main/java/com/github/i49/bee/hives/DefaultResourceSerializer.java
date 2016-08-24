package com.github.i49.bee.hives;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.github.i49.bee.web.ResourceSerializer;

public class DefaultResourceSerializer implements ResourceSerializer {

	private final TransformerFactory transformFactory; 
	private final Transformer transformer;
	
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	public DefaultResourceSerializer() {
		this.transformFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		}
		this.transformer = transformer;
	}

	@Override
	public byte[] writeHtmlDocument(Document document) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Source source = new DOMSource(document);
		StreamResult result = new StreamResult(stream);
		try {
			transformer.transform(source, result);
			return stream.toByteArray();
		} catch (TransformerException e) {
			e.printStackTrace();
			return null;
		}
	}

}
