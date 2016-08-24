package com.github.i49.bee.web;

import org.w3c.dom.Document;

public interface ResourceSerializer {

	byte[] writeHtmlDocument(Document document);
}
