package com.snda.storage.service.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import com.snda.storage.service.model.StorageBucket;
import com.snda.storage.service.utils.XmlResponsesSaxParser.ListAllMyBucketsHandler;


public class XmlResponsesSaxParserTest {
	
	private static final Map<String, Boolean> map = Maps.newHashMap();
	
	@Before
	public void initMap() {
		map.put("java_sdk_bucket_test1", true);
		map.put("wolegequ", true);
		map.put("wolegequa", true);
		map.put("wolegequaaa", true);
		map.put("wolegequaaaa", true);
	}
	
	@Test
	public void testParseListMyBucketsResponse() throws IOException {
		InputStream input = new FileInputStream("src/test/resources/bucket_list.xml");
		ListAllMyBucketsHandler handler = new XmlResponsesSaxParser().parseListMyBucketsResponse(input);
		StorageBucket[] buckets = handler.getBuckets();
		assertEquals(5, buckets.length);
		
		for (StorageBucket item : buckets) {
			assertTrue(map.containsKey(item.getName()));
		}
	}
}
