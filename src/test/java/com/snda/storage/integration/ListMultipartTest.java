package com.snda.storage.integration;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.security.SNDACredentials;
import com.snda.storage.service.CSService;
import com.snda.storage.service.impl.rest.httpclient.RestCSService;
import com.snda.storage.service.model.MultipartUpload;

public class ListMultipartTest {
	
	private static String access_key_id = "llap";
	private static String access_key_secret = "80456636-1f1d-42d8-961c-5088fadb38db";
	
	private static ProviderCredentials cred;
	private static CSService csService;
	
	@Before
	public void before() {
		cred = new SNDACredentials(access_key_id, access_key_secret);
		csService = new RestCSService(cred);
	}

	@Test
	public void listMultiparts() {
		String bucket = "";
		List<MultipartUpload> multipartListUploads = csService.multipartListUploads(bucket);
		for (MultipartUpload part : multipartListUploads) {
			System.out.println(part.getUploadId());
			System.out.println(part.getObjectKey());
		}
		System.out.println(multipartListUploads.size());
	}

}
