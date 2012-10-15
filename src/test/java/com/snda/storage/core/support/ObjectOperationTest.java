package com.snda.storage.core.support;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static com.snda.storage.core.support.HttpDateTimeFormatter.*;
import java.io.IOException;
import java.io.InputStream;
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
import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.StorageClass;
import com.snda.storage.core.Condition;
import com.snda.storage.core.ContentRange;
import com.snda.storage.core.CopyObjectRequest;
import com.snda.storage.core.CopySource;
import com.snda.storage.core.GetObjectRequest;
import com.snda.storage.core.MetadataDirective;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.core.Range;
import com.snda.storage.core.ResponseOverride;
import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;
import com.snda.storage.xml.CopyObjectResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class ObjectOperationTest {

	@Mock
	private HttpInvoker invoker;

	private GenericStorageService client;
	private DateTime now;
	private String bucket;
	private String key;
	private String endpoint;
	private Location location;

	@Test
	public void testDeleteObject() {
		client.deleteObject(bucket, key);
		verify(invoker).invoke(Request.builder().
				method(Method.DELETE).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				header("Date", formatDateTime(now)).
				build(), 
				Void.class);
	}
	
	@Test
	public void testDownloadObject() {
		Response response = mock(Response.class);
 		InputStream inputStream = mock(InputStream.class);
		when(response.getHeaders()).thenReturn(ImmutableMap.of(
					"ETag", "eee",
					"Content-Length", "123456789",
					"Content-Encoding", "aaaa",
					"x-snda-meta-name-1", "value1",
					"x-snda-meta-name-2", "value2"));
		when(response.getInputStream()).thenReturn(inputStream);
		
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				header("Date", formatDateTime(now)).
				build(), 
				Response.class)).thenReturn(response);
		
		
		SNDAObject actual = client.downloadObject(bucket, key);
		SNDAObject expected = new SNDAObject().
				withBucket(bucket).
				withKey(key).
				withContent(inputStream).
				withObjectMetadata(new SNDAObjectMetadata().
						withContentLength(123456789L).
						withETag("eee").
						withContentEncoding("aaaa").
						withMetadata("x-snda-meta-name-1", "value1").
						withMetadata("x-snda-meta-name-2", "value2"));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDownloadObjectWithParameter() {
		Response response = mock(Response.class);
		InputStream inputStream = mock(InputStream.class);
		when(response.getHeaders()).thenReturn(ImmutableMap.of(
				"ETag", "eee",
				"Content-Length", "10",
				"Content-Type", "application/xml",
				"Content-Range", "bytes 10-20/123456789"));
		when(response.getInputStream()).thenReturn(inputStream);
		
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("response-content-type", "application/xml").
				header("Range", "bytes=10-20").
				header("If-Match", "xxx").
				header("Date", formatDateTime(now)).
				build(), 
				Response.class)).thenReturn(response);
		
		
		SNDAObject actual = client.downloadObject(bucket, key, new GetObjectRequest().
				withRange(new Range(10, 20)).
				withCondition(new Condition().withIfMatch("xxx")).
				withResponseOverride(new ResponseOverride().withContentType("application/xml")));
		SNDAObject expected = new SNDAObject().
				withBucket(bucket).
				withKey(key).
				withContent(inputStream).
				withObjectMetadata(new SNDAObjectMetadata().
						withContentLength(10L).
						withETag("eee").
						withContentType("application/xml").
						withContentRange(new ContentRange(10, 20, 123456789)));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testHeadObjectWithParameter() throws IOException {
		Response response = mock(Response.class);
		InputStream inputStream = mock(InputStream.class);
		DateTime ifModifiedSince = new DateTime();
		when(response.getHeaders()).thenReturn(ImmutableMap.of(
				"ETag", "eee",
				"Content-Length", "10",
				"Content-Type", "application/xml",
				"x-snda-expiration-days", "15"));
		when(response.getInputStream()).thenReturn(inputStream);
		
		when(invoker.invoke(Request.builder().
				method(Method.HEAD).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				parameter("response-content-type", "application/xml").
				header("If-Match", "xxx").
				header("If-Modified-Since", formatDateTime(ifModifiedSince)).
				header("Date", formatDateTime(now)).
				build(), 
				Response.class)).thenReturn(response);
		
		
		SNDAObjectMetadata actual = client.headObject(bucket, key, new GetObjectRequest().
				withCondition(new Condition().
						withIfMatch("xxx").
						withIfModifiedSince(ifModifiedSince)).
				withResponseOverride(new ResponseOverride().withContentType("application/xml")));
		SNDAObjectMetadata expected = new SNDAObjectMetadata().
						withContentLength(10L).
						withETag("eee").
						withContentType("application/xml").
						withExpirationDays(15);
		assertEquals(expected, actual);
		verify(response).close();
	}
	
	@Test
	public void testUploadObject() {
		Entity entity = mock(Entity.class);
		Response response = mock(Response.class);
		when(response.getHeaders()).thenReturn(ImmutableMap.of("ETag", "1234567890"));
		when(invoker.invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				header("Cache-Control", "private").
				header("Content-Type", "text/html").
				header("Content-MD5", "MD").
				header("x-snda-storage-class", "REDUCED_REDUNDANCY").
				header("x-snda-meta-name-1", "value1").
				header("x-snda-meta-name-2", "value2").
				header("Date", formatDateTime(now)).
				entity(entity).
				build(), 
				Response.class)).thenReturn(response);
		
		UploadObjectResult actual = client.uploadObject(bucket, key, new UploadObjectRequest().
				withContentMD5("MD").
				withEntity(entity).
				withObjectCreation(new ObjectCreation().
						withCacheControl("private").
						withContentType("text/html").
						withStorageClass(StorageClass.REDUCED_REDUNDANCY).
						withMetadata("x-snda-meta-name-1", "value1").
						withMetadata("x-snda-meta-name-2", "value2")));
		UploadObjectResult expected = new UploadObjectResult("1234567890");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCopyObject() {
		CopyObjectResult expected = mock(CopyObjectResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				header("x-snda-copy-source", "source-bucket/112233").
				header("x-snda-copy-source-if-match", "yyyy").
				header("Date", formatDateTime(now)).
				build(), 
				CopyObjectResult.class)).thenReturn(expected);
		
		
		CopyObjectResult actual = client.copyObject(bucket, key, new CopyObjectRequest().
				withCopyCondition(new Condition().withIfMatch("yyyy")).
				withCopySource(new CopySource("source-bucket", "112233")));
		assertEquals(expected, actual);
	}
	
	@Test
	public void testCopyObjectWithReplaceMetadata() {
		CopyObjectResult expected = mock(CopyObjectResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				key(key).
				header("x-snda-copy-source", "source-bucket/112233").
				header("x-snda-copy-source-if-match", "yyyy").
				header("x-snda-metadata-directive", "REPLACE").
				header("Content-Type", "ttttt").
				header("x-snda-meta-name", "value").
				header("Date", formatDateTime(now)).
				build(), 
				CopyObjectResult.class)).thenReturn(expected);
		
		CopyObjectResult actual = client.copyObject(bucket, key, new CopyObjectRequest().
				withCopyCondition(new Condition().withIfMatch("yyyy")).
				withCopySource(new CopySource("source-bucket", "112233")).
				withMetadataDirective(MetadataDirective.REPLACE).
				withObjectCreation(new ObjectCreation().
						withContentType("ttttt").
						withMetadata("x-snda-meta-name", "value")));
		assertSame(expected, actual);
	}
	
	@Before
	public void setUp() {
		location = Location.HUABEI_1;
		endpoint = location.getEndpoint();
		now = new DateTime();
		bucket = "test-bucket";
		key = UUID.randomUUID().toString();
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
