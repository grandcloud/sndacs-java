package com.snda.storage.service.model;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

public class StorageBucketTest {

	private static StorageBucket storageBucket = null;
	private static final Date date = new Date();
	
	@Before
	public void createStorageBucket() {
		storageBucket = new StorageBucket("testBucket");
		storageBucket.addMetadata("Date", date);
		storageBucket.addMetadata("meta1", "value1");
	}
	
	@Test
	public void testToString() {
		String expect = "StorageBucket [name=testBucket] Metadata={meta1=value1, Date=" + date.toString() + "}";
		assertEquals(expect, storageBucket.toString());
	}

	@Test
	public void testGetCreationDate() {
		assertTrue(date.equals(storageBucket.getCreationDate()));
	}

	@Test
	public void testSetCreationDate() {
		Date creation = new Date();
		storageBucket.setCreationDate(creation);
		assertTrue(creation.equals(storageBucket.getCreationDate()));
	}

	@Test
	public void testGetName() {
		assertTrue("testBucket".equals(storageBucket.getName()));
	}

	@Test
	public void testSetName() {
		StorageBucket reset = new StorageBucket();
		reset.setName("reset");
		assertTrue("reset".equals(reset.getName()));
	}

	@Test
	public void testGetMetadataMap() {
		String expect = "{meta1=value1, Date=" + date.toString() + "}";
		assertEquals(expect, storageBucket.getMetadataMap().toString());
	}

	@Test
	public void testGetMetadata() {
		assertEquals("value1", storageBucket.getMetadata("meta1"));
	}

	@Test
	public void testContainsMetadata() {
		assertTrue(storageBucket.containsMetadata("Date"));
		assertTrue(!storageBucket.containsMetadata("date"));
	}

	@Test
	public void testAddMetadata() {
		Date sndaDate = new Date();
		storageBucket.addMetadata("snda-date", sndaDate);
		storageBucket.addMetadata("snda-meta", "snda-value");
		
		assertTrue(sndaDate.equals(storageBucket.getMetadata("snda-date")));
		assertTrue("snda-value".equals(storageBucket.getMetadata("snda-meta")));
	}

	@Test
	public void testAddAllMetadata() {
		Map<String, Object> metadatas = Maps.newHashMap();
		Date sndaDate = new Date();
		metadatas.put("snda-date", sndaDate);
		metadatas.put("snda-meta", "snda-value");
		storageBucket.addAllMetadata(metadatas);
		
		assertTrue(storageBucket.containsMetadata("snda-date"));
		assertTrue(storageBucket.containsMetadata("snda-meta"));
	}

	@Test
	public void testRemoveMetadata() {
		storageBucket.removeMetadata("meta1");
		assertTrue(!storageBucket.containsMetadata("meta1"));
	}

	@Test
	public void testReplaceAllMetadata() {
		Map<String, Object> metadatas = Maps.newHashMap();
		Date sndaDate = new Date();
		metadatas.put("snda-date", sndaDate);
		metadatas.put("snda-meta", "snda-value");
		storageBucket.replaceAllMetadata(metadatas);
		
		assertTrue(storageBucket.containsMetadata("snda-date"));
		assertTrue(storageBucket.containsMetadata("snda-meta"));
		
		assertTrue(!storageBucket.containsMetadata("Date"));
		assertTrue(!storageBucket.containsMetadata("meta1"));
	}

}
