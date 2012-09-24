package com.snda.storage;
import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class PresignedURIBuilderTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PresignedURIBuilderTest.class);
	
	private static final DateTimeZone CHINA_TIME = DateTimeZone.forOffsetHours(8);

	@Test
	public void test() throws URISyntaxException {
		URI uri = new PresignedURIBuilder().
<<<<<<< HEAD
			credential("norther", "1234567890").
=======
<<<<<<< HEAD
			credential("norther", "1234567890").
=======
			credential(new Credential("norther", "1234567890")).
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
			location(Location.HUADONG_1).
			bucket("mybucket").
			key("data/中文/key123456789").
			expires(new DateTime(2012, 9, 19, 16, 53, 0, 0).withZone(CHINA_TIME)).
			build();
		LOGGER.info("URI -> {}", uri);
		String expected = "http://storage-huadong-1.sdcloud.cn/mybucket/data/%E4%B8%AD%E6%96%87/key123456789?" +
				"Expires=1348044780&" +
				"SNDAAccessKeyId=norther&" +
				"Signature=SJawXv5QdQHcFrTqnx3RpmTN9WI%3D";
		assertEquals(new URI(expected), uri);
	}
	
	@Test
	public void testWithOverridedResponse() throws URISyntaxException {
		URI uri = new PresignedURIBuilder().
<<<<<<< HEAD
				credential("norther", "1234567890").
=======
<<<<<<< HEAD
				credential("norther", "1234567890").
=======
				credential(new Credential("norther", "1234567890")).
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
				location(Location.HUADONG_1).
				bucket("mybucket").
				key("key123456789").
				expires(new DateTime(2012, 9, 19, 16, 55, 0, 0).withZone(CHINA_TIME)).
				responseContentType("text/html").
				build();
		LOGGER.info("URI -> {}", uri);
		String expected = "http://storage-huadong-1.sdcloud.cn/mybucket/key123456789?" +
				"response-content-type=text%2Fhtml&" +
				"Expires=1348044900&" +
				"SNDAAccessKeyId=norther&" +
				"Signature=NA%2F%2BCEvjbuZbuHssJXv0HhRnsjo%3D";
		assertEquals(new URI(expected), uri);
	}
	
	public static void main(String[] args) {
		System.out.println(new DateTime(0L).plusSeconds(1348044399));
	}
}
