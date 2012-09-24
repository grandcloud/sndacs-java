package com.snda.storage.service.model;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class StorageObjectTest {
	
	private static StorageObject storageObject = null;
	
	@Before
	public void createObject() throws NoSuchAlgorithmException, IOException {
		storageObject = new StorageObject("testObject", "temp contents");
		storageObject.setBucketName("testBucket");
	}

	@Test
	public void testClone() throws IOException {
		StorageObject clone = (StorageObject) storageObject.clone();
		assertEquals("testObject", clone.getKey());
		
		BufferedReader br = new BufferedReader(new InputStreamReader(clone.getDataInputStream()));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ( (line = br.readLine()) != null ) {
			sb.append(line);
		}
		assertEquals("temp contents", sb.toString());
	}

	@Test
	public void testGetBucketName() {
		assertEquals("testBucket", storageObject.getBucketName());
	}

	@Test
	public void testSetBucketName() {
		storageObject.setBucketName("reset");
		assertEquals("reset", storageObject.getBucketName());
	}

	@Test
	public void testDataInputStream() throws IOException {
		InputStream input = new ByteArrayInputStream("temp contents".getBytes());
		storageObject.setDataInputStream(input);
		assertEquals(input.hashCode(), storageObject.getDataInputStream().hashCode());
		
		storageObject.closeDataInputStream();
		assertEquals(null, storageObject.getDataInputStream());
	}

	@Test
	public void testDataInputFile() {
		String filename = UUID.randomUUID().toString();
		File inputFile = new File(filename);
		storageObject.setDataInputFile(inputFile);
		assertEquals(inputFile.hashCode(), storageObject.getDataInputFile().hashCode());
		inputFile.delete();
	}

	@Test
	public void testGetMd5HashAsHex() {
		byte[] md5 = UUID.randomUUID().toString().getBytes();
		storageObject.setMd5Hash(md5);
		String md5get = storageObject.getMd5HashAsHex();
		String md5hex = Hex.encodeHexString(md5);
		assertEquals(md5hex, md5get);
	}

	@Test
	public void testGetMd5HashAsBase64() {
		byte[] md5 = UUID.randomUUID().toString().getBytes();
		storageObject.setMd5Hash(md5);
		byte[] md5get = Base64.decodeBase64(storageObject.getMd5HashAsBase64());
		assertEquals(md5.length, md5get.length);
		
		for (int i = 0; i < md5.length; i++) {
			assertEquals(md5[i], md5get[i]);
		}
	}

	@Test
	public void testGetLastModifiedDate() throws InterruptedException {
		DateTime dateTime = new DateTime();
		
		storageObject.addMetadata("Date", dateTime.toString());
		
		Date lastModifiedDate = storageObject.getLastModifiedDate();
		assertEquals(dateTime.getMillis() / 1000, lastModifiedDate.getTime() / 1000);
		
		Thread.sleep(100);
		Date modifiedTime = new Date();
		
		storageObject.setLastModifiedDate(modifiedTime);
		lastModifiedDate = storageObject.getLastModifiedDate();
		
		assertEquals(modifiedTime.getTime(), lastModifiedDate.getTime());
	}

	@Test
	public void testGetContentLength() {
		long length = new Random(System.currentTimeMillis()).nextLong();
		storageObject.setContentLength(length);
		
		assertEquals(length, storageObject.getContentLength());
	}

	@Test
	public void testGetContentType() throws NoSuchAlgorithmException, IOException {
		String filename = UUID.randomUUID().toString() + ".txt";
		File file = new File(filename);
		FileOutputStream output = new FileOutputStream(file);
		output.write(UUID.randomUUID().toString().getBytes());
		output.close();
		
		StorageObject objectForType = new StorageObject(file);
		assertEquals("text/plain", objectForType.getContentType());
		
		file.delete();
		
		objectForType.setContentType("TempType");
		assertEquals("TempType", objectForType.getContentType());
	}

	@Test
	public void testGetKey() {
		String key = UUID.randomUUID().toString();
		storageObject.setKey(key);
		
		assertEquals(key, storageObject.getKey());
	}

	@Test
	public void testGetETag() {
		String etag = UUID.randomUUID().toString();
		storageObject.setETag(etag);
		
		assertEquals(etag, storageObject.getETag());
	}

	@Test
	public void testIsMetadataComplete() {
		storageObject.setMetadataComplete(true);
		assertEquals(true, storageObject.isMetadataComplete());
	}

	@Test
	public void testGetModifiableMetadata() {
		Random random = new Random(System.currentTimeMillis());
		// add un-modifiable metadata
		storageObject.setContentLength(random.nextLong());
		storageObject.addMetadata("Date", new DateTime().toDateTimeISO().toString());
		storageObject.addMetadata("ETag", UUID.randomUUID().toString());
		storageObject.addMetadata("Last-Modified", new DateTime().minusDays(1).toDateTimeISO().toString());
		storageObject.addMetadata("id-2", UUID.randomUUID().toString());
		storageObject.addMetadata("request-id", UUID.randomUUID().toString());
		// add modifiable metadata
		storageObject.addMetadata(UUID.randomUUID().toString(), Long.toString(random.nextLong()));
		storageObject.addMetadata(UUID.randomUUID().toString(), Long.toString(random.nextLong()));
		
		int total = storageObject.getMetadataMap().size();
		assertEquals(total - 6, storageObject.getModifiableMetadata().size());
	}

}
