package com.github.i49.bee.web;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class DocumentWriter {

	private static final TransformerFactory transformFactory = TransformerFactory.newInstance();
	private static final String DEFAULT_ENCODING = "UTF-8";
	
	public void writeTo(OutputStream stream, Document document) throws IOException, TransformerException {
		Transformer transformer = createTransformer();
		Source source = new DOMSource(document);
		StreamResult result = new StreamResult(stream);
		transformer.transform(source, result);
	}

	private static Transformer createTransformer() throws TransformerConfigurationException {
		Transformer transformer = transformFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, DEFAULT_ENCODING);
		transformer.setOutputProperty(OutputKeys.METHOD, "html");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		return transformer;
	}
}
