package com.snda.storage.core.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableMap;
import com.snda.storage.Entity;
import com.snda.storage.Location;
import com.snda.storage.StorageClass;
import com.snda.storage.core.CopyPartRequest;
import com.snda.storage.core.CopySource;
import com.snda.storage.core.ListPartsCriteria;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.core.Range;
import com.snda.storage.core.UploadPartRequest;
import com.snda.storage.core.UploadPartResult;
import com.snda.storage.xml.CompleteMultipartUpload;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.CopyPartResult;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.ListPartsResult;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MultipartUploadOperationTest {

	@Mock
	private HttpInvoker invoker;

	private GenericStorageService client;
	private DateTime now;
	private String bucket;
	private String key;
	private String endpoint;
	private Location location;

	private String uploadId;
	
	@Test
	public void testInitiateMultipartUpload() {
		ObjectCreation objectCreation = new ObjectCreation().
				withStorageClass(StorageClass.REDUCED_REDUNDANCY).
				withContentType("test/aaa").
				withExpires("xyz");
		
		InitiateMultipartUploadResult expected = mock(InitiateMultipartUploadResult.class);
		
		when(invoker.invoke(Request.builder().
				method(Method.POST).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				subResource("uploads").
				header("x-snda-storage-class", "REDUCED_REDUNDANCY").
				header("Content-Type", "test/aaa").
				header("Expires", "xyz").
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				build(),
				InitiateMultipartUploadResult.class)).thenReturn(expected);
		
		InitiateMultipartUploadResult actual = client.initiateMultipartUpload(bucket, key, objectCreation);
		assertSame(expected, actual);
	}
	
	@Test
	public void testUploadPart() throws IOException {
		Entity entity = mock(Entity.class);
		Response response = mock(Response.class);
		when(response.getHeaders()).thenReturn(ImmutableMap.of("ETag", "etag1234567890"));
		when(invoker.invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("uploadId", uploadId).
				parameter("partNumber", "10").
				header("Content-MD5", "MD").
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				entity(entity).
				build(),
				Response.class)).thenReturn(response);
		
		UploadPartResult actual = client.uploadPart(bucket, key, uploadId, 10, new UploadPartRequest().
				withContentMD5("MD").
				withEntity(entity));
		UploadPartResult expected = new UploadPartResult("etag1234567890");
		assertEquals(expected, actual);
		verify(response).close();
	}
	
	@Test
	public void testCopyPart() throws IOException {
		CopyPartResult expected = mock(CopyPartResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("uploadId", uploadId).
				parameter("partNumber", "250").
				header("x-snda-copy-source", "mybucket/key1").
				header("x-snda-copy-source-range", "bytes=200-500").
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				build(),
				CopyPartResult.class)).thenReturn(expected);
		
		CopyPartResult actual = client.copyPart(bucket, key, uploadId, 250, new CopyPartRequest().
				withCopySource(new CopySource("mybucket", "key1")).
				withCopySourceRange(new Range(200, 500)));
		assertSame(expected, actual);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCompleteMultipartUpload() {
		List<Part> parts = mock(List.class);
		CompleteMultipartUploadResult expected = mock(CompleteMultipartUploadResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.POST).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("uploadId", uploadId).
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				entity(new CompleteMultipartUpload(parts)).
				build(),
				CompleteMultipartUploadResult.class)).thenReturn(expected);
		
		CompleteMultipartUploadResult actual = client.completeMultipartUpload(bucket, key, uploadId, parts);
		assertSame(expected, actual);
	}
	
	@Test
	public void testAbortMultipartUpload() {
		client.abortMultipartUpload(bucket, key, uploadId);
		
		verify(invoker).invoke(Request.builder().
				method(Method.DELETE).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("uploadId", uploadId).
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				build(), 
				Void.class);
	}
	
	@Test
	public void testListParts() {
		ListPartsResult expected = mock(ListPartsResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("uploadId", uploadId).
				parameter("part-number-marker", 255).
				header("Date", HttpDateTimeFormatter.formatDateTime(now)).
				build(), 
				ListPartsResult.class)).thenReturn(expected);
		
		ListPartsResult actual = client.listParts(bucket, key, uploadId, new ListPartsCriteria().
				withPartNumberMarker(255));
		assertSame(expected, actual);
	}
	
	@Before
	public void setUp() {
		location = Location.HUABEI_1;
		endpoint = location.getEndpoint();
		now = new DateTime();
		bucket = "test-bucket";
		key = UUID.randomUUID().toString();
		uploadId = "uuuuuuuu-id";
		client = new GenericStorageService(invoker) {
			@Override
			protected DateTime now() {
				return now;
			}

			@Override
			public Location getBucketLocation(String bucket) {
				return location;
			}
		};
	}
}
