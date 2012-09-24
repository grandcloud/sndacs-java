package com.snda.storage.service.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class CSBucketTest {
	
	private static CSBucket bucket;
	private static Date date;
	
	@Before
	public void createBucket() {
		date = new Date();
		bucket = new CSBucket("testBucket", "wuxi");
		bucket.setCreationDate(date);
	}

	@Test
	public void testToString() {
		String expect = "CSBucket [name=testBucket" 
						+ ",location=wuxi" 
						+ ",creationDate=" + date.toString()
				        + "] Metadata={Date=" + date.toString() +"}";
		assertEquals(expect, bucket.toString());
	}

	@Test
	public void testIsLocationKnown() {
		assertTrue(bucket.isLocationKnown());
	}

	@Test
	public void testGetLocation() {
		String location = UUID.randomUUID().toString();
		bucket.setLocation(location);
		assertEquals(location, bucket.getLocation());
	}

}
