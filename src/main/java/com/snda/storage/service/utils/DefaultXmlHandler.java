package com.snda.storage.service.utils;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultXmlHandler extends DefaultHandler {
	private StringBuffer currText = null;

    public void startDocument() {}

    public void endDocument() {}

    public void startElement(String uri, String name, String qName, Attributes attrs) {
        this.currText = new StringBuffer();
        this.startElement(name, attrs);
    }

    public void startElement(String name, Attributes attrs) {
         this.startElement(name);
     }

    public void startElement(String name) { }

    public void endElement(String uri, String name, String qName) {
        String elementText = this.currText.toString();
        this.endElement(name, elementText);
    }

    public void endElement(String name, String content) { }

    public void characters(char ch[], int start, int length) {
        this.currText.append(ch, start, length);
    }
}
