package com.ikeyit.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.regex.Pattern;

public class XmlUtils {
	private static final Logger log = LoggerFactory.getLogger(XmlUtils.class);
	static Pattern imgUrlPattern = Pattern.compile("<img src=\"([^\"]+)\"");

	@SuppressWarnings("serial")
	public static class SAXTerminatorException extends SAXException {

	}
	
	private static DocumentBuilder builder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static SAXParser saxParser() {
		try {
			return SAXParserFactory.newInstance().newSAXParser();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public static Document newDocument() {
		return builder().newDocument();
	}
	
	public static Document parseDocument(String xml) throws SAXException, IOException {
		InputSource is = new InputSource(new StringReader(xml));
		Document doc = builder().parse(is);
		return doc;
	}
	
	public static Document parseDocument(InputStream in) throws SAXException, IOException {
		InputSource is = new InputSource(in);
		Document doc = builder().parse(is);
		return doc;
	}
	
	public static Element addElement(Node node, String tagName) {
		return addElement(node, tagName, null);
	}
	
	public static Element addElement(Node node, String tagName, Object text) {
		Element element = node.getOwnerDocument().createElement(tagName);
		if (text != null)
			element.setTextContent(text.toString());
		node.appendChild(element);
		return element;
	}
	
	public static String firstElementText(Document document, String tagName) {
		NodeList elements = document.getElementsByTagName(tagName);
		if (elements.getLength() > 0)
			return ((Element)elements.item(0)).getTextContent();
		
		return null;
	}
	public static String firstChildElementText(Node node, String tagName) {
		Element element = firstChildElement(node, tagName);
		return element == null ? null : element.getTextContent();
	}
	
	public static Element firstChildElement(Node node, String tagName) {
		for (Node tempNode = node.getFirstChild(); tempNode != null; tempNode = tempNode.getNextSibling()) {
			if (tempNode.getNodeType() == Node.ELEMENT_NODE && tempNode.getNodeName().equals(tagName)) {
				return (Element) tempNode;
			}
		}
		return null;
	}

	
	public static String[] elementsText(Element element, String tagName ) {
		NodeList elements = element.getElementsByTagName(tagName);
		String[] texts = new String[elements.getLength()];
		for (int i = 0; i < texts.length; i++) {
			texts[i] = elements.item(i).getTextContent();
		}
		return texts;
	}
}
