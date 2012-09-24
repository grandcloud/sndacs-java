package com.snda.storage.integration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.primitives.Bytes;
<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import com.snda.storage.Credential;
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
import com.snda.storage.Entity;
import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.SNDAServiceException;
import com.snda.storage.SNDAStorage;
import com.snda.storage.SNDAStorageBuilder;
import com.snda.storage.core.ContentRange;
import com.snda.storage.core.Range;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ObjectOperationTest {

	private SNDAStorage storage;

	private String bucket;
	private String key;
	private ByteEntity entity;

	private String contentType;
	private Map<String, Object> metadata;

	private String contentMD5;
	
	@Before
	public void setUp() {
		bucket = "beijing";
		key = "中文目录/folder/中文2/" + UUID.randomUUID().toString();
		contentType = "application/octet-stream";
		metadata = ImmutableMap.<String, Object> of(
				"x-snda-meta-test", "object-test", 
				"x-snda-meta-random", UUID.randomUUID().toString());
		int contentLength = 512;
		entity = new ByteEntity(RandomStringUtils.randomAscii(contentLength).getBytes());
		storage = new SNDAStorageBuilder().
<<<<<<< HEAD
				credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm").
=======
<<<<<<< HEAD
				credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm").
=======
				credential(new Credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm")).
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
				https().
				build();
		putObject();
	}
	
	@After
	public void tearDown() {
		deleteObject();
		storage.destory();
	}
	
	private void deleteObject() {
		storage.bucket(bucket).object(key).delete();

	}

	@Test
	public void testConditionalGetForLastModified() {
		DateTime lastModified = storage.bucket(bucket).object(key).head().getLastModified();
		
		try {
			storage.bucket(bucket).object(key).ifModifiedSince(lastModified).head();
			fail();
		} catch (SNDAServiceException e) {
			assertEquals(304, e.getStatus());
		}

		storage.bucket(bucket).object(key).ifModifiedSince(lastModified.minusMinutes(5)).head();
		
		storage.bucket(bucket).object(key).ifUnmodifiedSince(lastModified).head();
		
		try {
			storage.bucket(bucket).object(key).ifUnmodifiedSince(lastModified.minusMinutes(5)).head();
			fail();
		} catch (SNDAServiceException e) {
			assertEquals(412, e.getStatus());
		}
	}

	@Test
	public void testConditionalGetForETag() {
		String eTag = storage.bucket(bucket).object(key).head().getETag();
		String fakeETag = eTag.replace(eTag.charAt(1), '!');

		storage.bucket(bucket).object(key).ifMatch(eTag).head();

		try {
			storage.bucket(bucket).object(key).ifMatch(fakeETag).head();
			fail();
		} catch (SNDAServiceException e) {
			assertEquals(412, e.getStatus());
		}

		storage.bucket(bucket).object(key).ifNoneMatch(fakeETag).head();

		try {
			storage.bucket(bucket).object(key).ifNoneMatch(eTag).head();
			fail();
		} catch (SNDAServiceException e) {
			assertEquals(304, e.getStatus());

		}
	}

	@Test
	public void testGetRange() throws IOException {
		List<List<Byte>> partitions = Lists.partition(Bytes.asList(entity.getBytes()), (int) entity.getContentLength() / 10);
		int total = 0;
		for (int i = 0; i < partitions.size(); i++) {
			List<Byte> parts = partitions.get(i);
			Range range = new Range(total, total + parts.size() - 1);
			assertGetRange(Bytes.toArray(parts), range);
			total += parts.size();
		}
	}

	private void assertGetRange(byte[] bytes, Range range) throws IOException {
		SNDAObject object = null;
		try {
			object = storage.bucket(bucket).object(key).range(range.getFirstBytePosition(), range.getLastBytePosition()).download();
			ContentRange contentRange = new ContentRange(range.getFirstBytePosition(), range.getLastBytePosition(), entity.getContentLength());
			assertEquals(contentRange, object.getObjectMetadata().getContentRange());
			assertEquals(bytes.length, object.getObjectMetadata().getContentLength());
			assertBytesEquals(bytes, ByteStreams.toByteArray(object.getContent()));
		} finally {
			Closeables.closeQuietly(object);
		}
	}
	
	@Test
	public void testGetObjectContent() throws IOException {
		SNDAObject object = null;
		try {
			object = storage.bucket(bucket).object(key).download();
			assertByteStreamEquals(entity.getInput(), object.getContent());
			assertEquals(metadata, object.getObjectMetadata().getMetadata());
			assertEquals(entity.getContentLength(), object.getObjectMetadata().getContentLength());
		} finally {
			Closeables.closeQuietly(object);
		}
	}

	@Test
	public void testGetObjectContentWithResponseOverriding() {
		String overridedContentType = "text/plain";
		String overridedContentDisposition = "mock.content.disposition";
		SNDAObjectMetadata objectMetadata = storage.bucket(bucket).object(key).
			responseContentType(overridedContentType).
			responseContentDisposition(overridedContentDisposition).
			head();
		assertEquals(metadata, objectMetadata.getMetadata());
		assertEquals(overridedContentType, objectMetadata.getContentType());
		assertEquals(overridedContentDisposition, objectMetadata.getContentDisposition());

	}

	@Test
	public void testHeadMetadata() {
		SNDAObjectMetadata objectMetadata = storage.bucket(bucket).object(key).head();
		assertEquals(metadata, objectMetadata.getMetadata());
		assertEquals(contentType, objectMetadata.getContentType());
		assertEquals(entity.getContentLength(), objectMetadata.getContentLength());
	}

	private void putObject() {
		storage.bucket(bucket).object(key).
			contentType(contentType).
			contentMD5(contentMD5).
			metadata(metadata).
			entity(entity).
			upload();
	}

	private static void assertByteStreamEquals(InputStream expected, InputStream actual) throws IOException {
		byte[] expectedBytes = ByteStreams.toByteArray(expected);
		byte[] actualBytes = ByteStreams.toByteArray(actual);
		assertBytesEquals(expectedBytes, actualBytes);
	}

	private static void assertBytesEquals(byte[] expected, byte[] actual) {
		assertEquals(Bytes.asList(expected), Bytes.asList(actual));
	}
	
	private static class ByteEntity implements Entity {

		private final byte[] bytes;
		
		public ByteEntity(byte[] bytes) {
			this.bytes = bytes;
		}

		@Override
		public long getContentLength() {
			return bytes.length;
		}

		@Override
		public InputStream getInput() throws IOException {
			return new ByteArrayInputStream(bytes);
		}

		public byte[] getBytes() {
			return bytes;
		}
		
	}
}
