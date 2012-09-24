package com.snda.storage.authorization;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class CanonicalizationTest {

	@Mock
	private CanonicalizableRequest request;

	private HashMultiMap<String, String> headers;
	private HashMultiMap<String, String> parameters;
	
	@Test
	public void testCanonicalizeWithResponseHeaderOverrided() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		parameters.putSingle("response-content-encoding", "gzip");
		parameters.putSingle("response-cache-control", "private");
		parameters.putSingle("response-content-type", "application/xml");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected =
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket/name?" +
					"response-cache-control=private&" +
					"response-content-encoding=gzip&" +
					"response-content-type=application/xml";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalizeWithSubRespurceAndResponseHeaderOverrided() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		parameters.putSingle("response-content-encoding", "gzip");
		parameters.putSingle("response-cache-control", "private");
		parameters.putSingle("response-content-type", "application/xml");
		parameters.putSingle("versionId", "789");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected =
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket/name?" +
					"response-cache-control=private&" +
					"response-content-encoding=gzip&" +
					"response-content-type=application/xml&" + 
					"versionId=789";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}

	@Test
	public void testCanonicalize() throws URISyntaxException {
		headers.putSingle("Content-MD5", "12345678");
		headers.putSingle("Content-Type", "text/xml");
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		headers.putSingle("Other", "Abcx");
		headers.add("x-sNDA-Mock", "Jack ");
		headers.add("x-sNDA-Mock", " Alex");
		headers.putSingle("X-SnDa-SPORT", "Football");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected =
				"GET\n" + 
				"12345678\n" + 
				"text/xml\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"x-snda-mock:Jack,Alex\n" + 
				"x-snda-sport:Football\n" + 
				"/bucket/name";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalize2() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		headers.putSingle("Other", "Abcx");
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket/name";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalize3() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalize4() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalizeBucketResource() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket");
		
		String expected =
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalizeWithExpires() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		parameters.putSingle("Expires", "1141889120");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"1141889120\n" + 
				"/bucket/name";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}

	@Test
	public void testCanonicalizeWithSubResource() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		parameters.putSingle("location", "");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket/name?location";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalizeWithMultiSubResource() throws URISyntaxException {
		headers.putSingle("Date", "Tue, 27 Mar 2007 19:42:41 +0000");
		
		parameters.putSingle("versionId", "789");
		parameters.putSingle("location", "");
		parameters.putSingle("key", "value");
		
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected = "GET\n" + 
				"\n" + 
				"\n" + 
				"Tue, 27 Mar 2007 19:42:41 +0000\n" + 
				"/bucket/name?location&versionId=789";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
	}
	
	@Test
	public void testCanonicalizeWithDateOverride() throws URISyntaxException {
		headers.putSingle("x-snda-date", "Tue, 27 Mar 2007 19:42:41 +0000");
		when(request.getMethod()).thenReturn("GET");
		when(request.getUndecodedPath()).thenReturn("/bucket/name");
		
		String expected = 
				"GET\n" + 
				"\n" + 
				"\n" + 
				"\n" + 
				"x-snda-date:Tue, 27 Mar 2007 19:42:41 +0000\n" +
				"/bucket/name";
		
		String value = Canonicalization.canonicalize(request);
		assertThat(value, is(expected));
		
	}

	@Before
	public void setUp() {
		headers = HashMultiMap.create();
		parameters = HashMultiMap.create();
		when(request.getHeaders()).thenReturn(headers);
		when(request.getParameters()).thenReturn(parameters);
	}
}
