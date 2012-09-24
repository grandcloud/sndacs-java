package com.snda.storage.core;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.snda.storage.core.ContentRange;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ContentRangeTest {

	@Test
			public void testParseContentRange() {
				ContentRange expected = new ContentRange(0, 499, 1234);
				ContentRange actual = ContentRange.parseContentRange("bytes 0-499/1234");
				assertEquals(expected, actual);
			}
	
	@Test
			public void testParseContentRange2() {
				ContentRange expected = new ContentRange(500, 1233, 1234);
				ContentRange actual = ContentRange.parseContentRange("bytes 500-1233/1234");
				assertEquals(expected, actual);
			}
	
	@Test(expected = IllegalArgumentException.class) 
			public void testParseContentRangeIllegalContentRange() {
				ContentRange.parseContentRange("bytes 500-1233/");
			}
	
	@Test(expected = IllegalArgumentException.class) 
			public void testParseContentRangeIllegalContentRange2() {
				ContentRange.parseContentRange("bytes 500-1233/A");
			}
	
	@Test(expected = IllegalArgumentException.class) 
			public void testParseContentRangeIllegalContentRange3() {
				ContentRange.parseContentRange("bytes 500--1233/A");
			}
}
