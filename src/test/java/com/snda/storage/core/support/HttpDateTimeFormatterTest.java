package com.snda.storage.core.support;
import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpDateTimeFormatterTest {

	private static final DateTimeZone CHINA_TIME = DateTimeZone.forOffsetHours(8);

	@Test
		public void testFormatDateTime() {
			String text = HttpDateTimeFormatter.formatDateTime(new DateTime(2012, 9, 19, 15, 14, 16).withZone(CHINA_TIME));
			assertEquals("Wed, 19 Sep 2012 07:14:16 GMT", text);
		}
	
	@Test
		public void testParseDateTime() {
			String text = "Wed, 19 Sep 2012 07:14:16 GMT";
			DateTime dateTime = new DateTime(2012, 9, 19, 15, 14, 16).withZone(CHINA_TIME);
			assertEquals(dateTime.getMillis(), HttpDateTimeFormatter.parseDateTime(text).getMillis());
		}
}
