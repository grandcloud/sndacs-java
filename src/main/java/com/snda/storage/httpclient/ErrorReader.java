package com.snda.storage.httpclient;
import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.snda.storage.core.support.Error;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ErrorReader implements MessageReader<Error> {

	private final DocumentBuilder documentBuilder;

	public ErrorReader() {
		this.documentBuilder = newDefaultDocumentBuilder();
	}

	@Override
	public boolean isReadable(Class<?> type) {
		return Error.class == type;
	}

	@Override
	public Error read(Class<Error> type, InputStream inputStream) {
		try {
			return new Error(read(inputStream));
		} catch (Exception e) {
			throw new IllegalStateException("Failed to unmarshal Error", e);
		}
	}

	public Map<String, String> read(InputStream inputStream) throws IOException, SAXException {
		byte[] bytes = ByteStreams.toByteArray(inputStream);
		if (bytes.length == 0) {
			return ImmutableMap.of();
		}
		Map<String, String> error = Maps.newHashMap();
		Document document = documentBuilder.parse(new ByteArrayInputStream(bytes));
		Node root = document.getFirstChild();
		checkError(root.getNodeName());
		NodeList childNodes = root.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				String key = node.getNodeName();
				String value = node.getTextContent();
				error.put(key, value);
			}
		}
		return ImmutableMap.copyOf(error);
	}

	private void checkError(String nodeName) {
		checkState("Error".equals(nodeName), "The error document contains an invalid root element: " + nodeName);
	}

	private static DocumentBuilder newDefaultDocumentBuilder() {
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IllegalStateException(e);
		}
	}

}
