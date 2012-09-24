package com.snda.storage.httpclient;
import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.snda.storage.xml.XMLEntity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ErrorReaderTest {

	private ErrorReader errorReader;
	
	@Test
	public void testRead() throws Exception {
		Map<String, String> expected = ImmutableMap.of(
				"Code", "NoSuchKey",
				"Message", "The resource you requested does not exist",
				"Resource", "/mybucket/somekey",
				"RequestId", "4442587FB7D0A2F9HJDSY23");
		
		assertEquals(expected, errorReader.read(XMLEntity.class.getResourceAsStream("error.xml")));
	}
	
	@Before
	public void setUp() {
		errorReader = new ErrorReader();
	}
}
