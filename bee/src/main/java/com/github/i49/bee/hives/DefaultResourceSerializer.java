package com.github.i49.bee.hives;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

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
	private final Charset encoding;
	
	public DefaultResourceSerializer(Charset encoding) {
		this.transformFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, encoding.name());
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
		this.transformer = transformer;
		this.encoding = encoding;
	}

	@Override
	public Charset getEncoding() {
		return encoding;
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
