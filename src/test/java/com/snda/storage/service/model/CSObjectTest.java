package com.snda.storage.service.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Before;
import org.junit.Test;

public class CSObjectTest {
	
	private static CSObject object;
	private static final String KEY = "testObject";
	private static final String BUCKET = "testBucket";
	private static final String CONTENTS = "temp contents";
	private static final String CONTENT_MD5 = "YX7ZjtISrKrkHk/LCUnNmA==";
	private static final String MD5_HASH = "617ed98ed212acaae41e4fcb0949cd98";
	private static final String CONTENT_TYPE = "text/plain";
	
	@Before
	public void createObject() throws NoSuchAlgorithmException, IOException {
		object = new CSObject(KEY, CONTENTS);
		object.setBucketName(BUCKET);
		object.setMetadataComplete(true);
	}

	@Test
	public void testClone() {
		CSObject clone = (CSObject) object.clone();
		assertEquals(object.getKey(), clone.getKey());
		assertEquals(object.getBucketName(), clone.getBucketName());
		assertEquals(object.isMetadataComplete(), clone.isMetadataComplete());
	}

	@Test
	public void testToString() {
		String expect = "CSObject [key=" + KEY + ", bucket=" + BUCKET
        			  + ", lastModified=" + null + ", dataInputStream=" + object.getDataInputStream()
        			  + ", Metadata={Content-Length=" + CONTENTS.length()
        			  + ", Content-MD5=" + CONTENT_MD5
        			  + ", md5-hash=" + MD5_HASH
        			  + ", Content-Type=" + CONTENT_TYPE
        			  + "; charset=utf-8}]";
		assertEquals(expect, object.toString());
	}

}
