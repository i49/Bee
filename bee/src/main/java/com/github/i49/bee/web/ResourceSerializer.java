package com.github.i49.bee.web;

import java.nio.charset.Charset;

import org.w3c.dom.Document;

public interface ResourceSerializer {

	Charset getEncoding();
	
	byte[] writeHtmlDocument(Document document);
}
