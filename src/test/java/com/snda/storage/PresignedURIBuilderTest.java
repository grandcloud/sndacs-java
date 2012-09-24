package com.snda.storage;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.core.Credential;
import com.snda.storage.core.StorageService;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class PresignedURIBuilderTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(PresignedURIBuilderTest.class);
	
	private static final DateTimeZone CHINA_TIME = DateTimeZone.forOffsetHours(8);

	@Mock
	private StorageService storageService;
	
	@Test
	public void test() throws URISyntaxException {
		URI uri = new PresignedURIBuilder(storageService).
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
		URI uri = new PresignedURIBuilder(storageService).
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
	
	@Before
	public void setUp() {
		when(storageService.getCredential()).thenReturn(new Credential("norther", "1234567890"));
		when(storageService.getBucketLocation("mybucket")).thenReturn(Location.HUADONG_1);
	}
}
