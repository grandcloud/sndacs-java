package com.snda.storage.service.utils;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class RestUtilsTest {

	private static Map<String, Object> headerMap = null;

	@Before
	public void createHeaderMap() {
		headerMap = Maps.newHashMap();
		headerMap.put("Date", "Wed, 12 Oct 2009 17:50:00 GMT");
		headerMap.put("Content-Type", "text/plain");
		headerMap.put("Content-Length", "11434");
		headerMap.put("prefix-1-myown-1", "own_1");
		headerMap.put("prefix-1-myown-2", "own_2");
		headerMap.put("prefix-1-myown-3", "own_3");
	}

	@Test
	public void testEncodeUrlString() {
		String url = "The string ü@foo-bar";
		String expect = "The%20string%20%C3%BC@foo-bar";
		assertEquals(expect, RestUtils.encodeUrlString(url));
	}

	@Test
	public void testEncodeUrlPath() {
		String url = "The string : ü:@:foo-bar";
		String expect = "The string %3A %C3%BC%3A@%3Afoo-bar";
		assertEquals(expect, RestUtils.encodeUrlPath(url, " "));
	}

	@Test
	public void testMakeServiceCanonicalString()
			throws UnsupportedEncodingException {
		String method = "METHOD_1";
		String resource = "BUCKET_1/my-note.txt";
		String expires = null;
		String headerPrefix = "prefix-1-";
		List<String> serviceResourceParameterNames = null;
		String canonicalString = RestUtils.makeServiceCanonicalString(method,
				resource, headerMap, expires, headerPrefix,
				serviceResourceParameterNames);

		String expect = "METHOD_1" +
				"\n\ntext/plain" +
				"\nWed, 12 Oct 2009 17:50:00 GMT" +
				"\nprefix-1-myown-1:own_1" +
				"\nprefix-1-myown-2:own_2" +
				"\nprefix-1-myown-3:own_3" +
				"\nBUCKET_1/my-note.txt";
		
		assertEquals(expect, canonicalString);
	}

}
