package com.snda.storage.service.impl.rest.httpclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Maps;
import com.snda.storage.security.SNDACredentials;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.utils.RestUtils;

@RunWith(MockitoJUnitRunner.class)
public class RestStorageServiceTest {
	
	private static final String accessKey = UUID.randomUUID().toString();
	private static final String secretKey = UUID.randomUUID().toString();
	private static final String friendlyName = "szwdsd";
	private static SNDACredentials credentials;
	private static RestStorageService service;
	
	private static RestStorageService spyService;
	
	@Before
	public void createService() {
		credentials = new SNDACredentials(accessKey, secretKey, friendlyName);
		service = new RestCSService(credentials);
		
		spyService = spy(service);
	}

	@Test
	public void testGetHttpClient() {
		assertNotNull(service.getHttpClient());
	}

	@Test
	public void testSetHttpClient() {
		HttpClient httpClient = RestUtils.initHttpConnection();
		service.setHttpClient(httpClient);
		
		assertEquals(httpClient.hashCode(), service.getHttpClient().hashCode());
	}

	@Test
	public void testAddRequestHeadersToConnectionAndRename() {
		
		HttpGet get = new HttpGet();
		Map<String, Object> headers = Maps.newHashMap();
		String key = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();
		headers.put(key, value);
		service.addRequestHeadersToConnection(get, headers);
		
		assertTrue(get.containsHeader(key));
		assertEquals(value, get.getFirstHeader(key).getValue());
		
		Map<String, Object> renameMetadataKeys = service.renameMetadataKeys(headers);
		assertTrue(renameMetadataKeys.containsKey(service.getRestMetadataPrefix() + key));
		assertEquals(value, renameMetadataKeys.get(service.getRestMetadataPrefix() + key));
	}

	@Test
	public void testAddMetadataToHeaders() {
		HttpGet get = new HttpGet();
		Map<String, Object> metadatas = Maps.newHashMap();
		String key = UUID.randomUUID().toString();
		String value = UUID.randomUUID().toString();
		metadatas.put(key, value);
		service.addMetadataToHeaders(get, metadatas);
		
		assertTrue(get.containsHeader(key));
		assertEquals(value, get.getFirstHeader(key).getValue());
	}

	@Test
	public void testIsXmlContentType() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File("src/test/resources/bucket_list.xml")));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ( (line = br.readLine()) != null ) {
			sb.append(line);
		}
		service.isXmlContentType(sb.toString());
	}

	@Test
	public void testShutdown() {
		service.shutdown();
		assertTrue(service.isShutdown());
	}

	@Test
	public void testGetProviderCredentials() {
		assertEquals(credentials.hashCode(), service.getProviderCredentials().hashCode());
	}

	@Test
	public void testIsObjectInBucket() {
		when(spyService.headObjectImpl(Matchers.anyString(), Matchers.anyString())).thenReturn(new CSObject());
		assertTrue(spyService.isObjectInBucket(Matchers.anyString(), Matchers.anyString()));
		
		when(spyService.headObjectImpl(Matchers.anyString(), Matchers.anyString())).thenReturn(null);
		assertTrue(!spyService.isObjectInBucket(Matchers.anyString(), Matchers.anyString()));
	}

	@Test
	public void testGetCurrentTime() {
		assertNotNull(service.getCurrentTime());
	}

}
