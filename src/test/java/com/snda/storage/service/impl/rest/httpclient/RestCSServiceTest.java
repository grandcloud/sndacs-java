package com.snda.storage.service.impl.rest.httpclient;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import com.snda.storage.security.SNDACredentials;

public class RestCSServiceTest {
	
	private static final String accessKey = UUID.randomUUID().toString();
	private static final String secretKey = UUID.randomUUID().toString();
	private static final String friendlyName = "szwdsd";
	
	private static SNDACredentials credentials;
	private static RestCSService service;
	
	@Before
	public void createService() {
		credentials = new SNDACredentials(accessKey, secretKey, friendlyName);
		service = new RestCSService(credentials);
	}

	@Test
	public void testGetRestHeaderPrefix() {
		assertEquals("x-snda-", service.getRestHeaderPrefix());
	}

	@Test
	public void testGetRestMetadataPrefix() {
		assertEquals("x-snda-meta-", service.getRestMetadataPrefix());
	}

	@Test
	public void testGetEndpoint() {
		assertEquals("storage.grandcloud.cn", service.getEndpoint());
	}

	@Test
	public void testGetHttpPort() {
		assertEquals(80, service.getHttpPort());
	}

	@Test
	public void testGetHttpsPort() {
		assertEquals(443, service.getHttpsPort());
	}

	@Test
	public void testGetSignatureIdentifier() {
		assertEquals("SNDA", service.getSignatureIdentifier());
	}

}
