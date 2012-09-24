package com.snda.storage.core.support;
import static com.snda.storage.ByteUnit.KB;
import static com.snda.storage.ByteUnit.MB;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.io.LimitInputStream;
import com.snda.storage.Entity;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;
import com.snda.storage.core.UploadPartRequest;
import com.snda.storage.core.UploadPartResult;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.Part;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class MultipartObjectUploaderTest {

	@Mock
	private StorageService client;
	
	private MultipartObjectUploader multipartObjectUploader;
	private String bucket;
	private String key;
	
	@Test
	public void testSinglePut() {
		Long contentLength = 1 * KB;
		Entity entity = when(mock(Entity.class).getContentLength()).thenReturn(contentLength).getMock();
		UploadObjectRequest putObjectRequest = when(mock(UploadObjectRequest.class).getEntity()).thenReturn(entity).getMock();
		
		UploadObjectResult result = mock(UploadObjectResult.class);
		when(client.uploadObject(bucket, key, putObjectRequest)).thenReturn(result);
		
		UploadObjectResult actual = multipartObjectUploader.putObject(bucket, key, putObjectRequest);
		assertSame(result, actual);
	}
	
	@Test
	public void testMultipartUpload() throws IOException {
		Long contentLength = 10 * MB;
		
		InputStream inputStream = mock(InputStream.class);
		
		Entity entity = mock(Entity.class);
		when(entity.getContentLength()).thenReturn(contentLength);
		when(entity.getInput()).thenReturn(inputStream);
		
		ObjectCreation objectCreation = mock(ObjectCreation.class);
		
		UploadObjectRequest putObjectRequest = new UploadObjectRequest().
				withEntity(entity).
				withObjectCreation(objectCreation);

		String uploadId = UUID.randomUUID().toString();
		
		when(client.initiateMultipartUpload(bucket, key, objectCreation)).thenReturn(new InitiateMultipartUploadResult().
				withBucket(bucket).
				withKey(key).
				withUploadId(uploadId));
		
		String eTag1 = "etag1";
		when(client.uploadPart(eq(bucket), eq(key), eq(uploadId), eq(1), expectedRequestWithLimitedInputStream(5 * MB, inputStream))).
			thenReturn(new UploadPartResult(eTag1));
		String eTag2 = "etag2";
		when(client.uploadPart(eq(bucket), eq(key), eq(uploadId), eq(2), expectedRequestWithLimitedInputStream(5 * MB, inputStream))).
			thenReturn(new UploadPartResult(eTag2));
		
		String eTag = "finalETag";
		CompleteMultipartUploadResult completeResult = new CompleteMultipartUploadResult().withEntityTag(eTag);
		when(client.completeMultipartUpload(bucket, key, uploadId, ImmutableList.of(
				new Part(1, eTag1),
				new Part(2, eTag2)))).thenReturn(completeResult);
		
		UploadObjectResult actual = multipartObjectUploader.putObject(bucket, key, putObjectRequest);
		UploadObjectResult expected = new UploadObjectResult(eTag);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testMultipartUploadWithRemainderPart() throws IOException {
		Long contentLength = 12 * MB;
		
		InputStream inputStream = mock(InputStream.class);
		
		Entity entity = mock(Entity.class);
		when(entity.getContentLength()).thenReturn(contentLength);
		when(entity.getInput()).thenReturn(inputStream);
		
		ObjectCreation objectCreation = mock(ObjectCreation.class);
		
		UploadObjectRequest putObjectRequest = new UploadObjectRequest().
				withEntity(entity).
				withObjectCreation(objectCreation);
		
		String uploadId = UUID.randomUUID().toString();
		
		when(client.initiateMultipartUpload(bucket, key, objectCreation)).thenReturn(new InitiateMultipartUploadResult().
				withBucket(bucket).
				withKey(key).
				withUploadId(uploadId));
		
		String eTag1 = "etag1";
		when(client.uploadPart(eq(bucket), eq(key), eq(uploadId), eq(1), expectedRequestWithLimitedInputStream(5 * MB, inputStream))).
			thenReturn(new UploadPartResult(eTag1));
		String eTag2 = "etag2";
		when(client.uploadPart(eq(bucket), eq(key), eq(uploadId), eq(2), expectedRequestWithLimitedInputStream(5 * MB, inputStream))).
			thenReturn(new UploadPartResult(eTag2));
		String eTag3 = "etag3";
		when(client.uploadPart(eq(bucket), eq(key), eq(uploadId), eq(3), expectedRequestWithInputStream(inputStream))).
			thenReturn(new UploadPartResult(eTag3));
		
		String eTag = "finalETag";
		CompleteMultipartUploadResult completeResult = new CompleteMultipartUploadResult().withEntityTag(eTag);
		when(client.completeMultipartUpload(bucket, key, uploadId, ImmutableList.of(
				new Part(1, eTag1),
				new Part(2, eTag2),
				new Part(3, eTag3)
				))).thenReturn(completeResult);
		
		UploadObjectResult actual = multipartObjectUploader.putObject(bucket, key, putObjectRequest);
		UploadObjectResult expected = new UploadObjectResult(eTag);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testAbort() throws IOException {
	Long contentLength = 7 * MB;
		
		InputStream inputStream = mock(InputStream.class);
		
		Entity entity = mock(Entity.class);
		when(entity.getContentLength()).thenReturn(contentLength);
		when(entity.getInput()).thenReturn(inputStream);
		
		ObjectCreation objectCreation = mock(ObjectCreation.class);
		
		UploadObjectRequest putObjectRequest = new UploadObjectRequest().
				withEntity(entity).
				withObjectCreation(objectCreation);
		
		String uploadId = UUID.randomUUID().toString();
		
		when(client.initiateMultipartUpload(bucket, key, objectCreation)).thenReturn(new InitiateMultipartUploadResult().
				withBucket(bucket).
				withKey(key).
				withUploadId(uploadId));
		
		when(client.uploadPart(
				anyString(), 
				anyString(), 
				anyString(), 
				anyInt(), 
				any(UploadPartRequest.class))).thenThrow(new RuntimeException("Expected"));
		
		try {
			multipartObjectUploader.putObject(bucket, key, putObjectRequest);
		} catch (RuntimeException e) {
			assertEquals("Expected", e.getMessage());
		}
		verify(client).abortMultipartUpload(bucket, key, uploadId);
	}

	private UploadPartRequest expectedRequestWithInputStream(final InputStream inputStream) {
		return argThat(new BaseMatcher<UploadPartRequest>() {
			@Override
			public boolean matches(Object object) {
				assertInstanceOf(object, UploadPartRequest.class);
				UploadPartRequest that = (UploadPartRequest) object;
				Entity entity = that.getEntity();
				assertInstanceOf(entity, InputStreamEntity.class);
				InputStreamEntity inputStreamEntity = (InputStreamEntity) entity;
				assertSame(inputStream, inputStreamEntity.getRawInputStream());
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("InputStream: " + inputStream);
			}
		});
	}

	private UploadPartRequest expectedRequestWithLimitedInputStream(final long size, final InputStream inputStream) {
		return argThat(new BaseMatcher<UploadPartRequest>() {
			@Override
			public boolean matches(Object object) {
				assertInstanceOf(object, UploadPartRequest.class);
				UploadPartRequest that = (UploadPartRequest) object;
				Entity entity = that.getEntity();
				assertInstanceOf(entity, InputStreamEntity.class);
				InputStreamEntity inputStreamEntity = (InputStreamEntity) entity;
				assertInstanceOf(inputStreamEntity.getRawInputStream(), LimitInputStream.class);
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("ContentLength: " + size + ", InputStream: " + inputStream);
			}
		});
	}
	
	protected void assertInstanceOf(Object object, Class<?> type) {
		if (!type.isInstance(object)) {
			fail("The object is not intance of type " + type);
		}
	}

	@Before
	public void setUp() {
		multipartObjectUploader = new MultipartObjectUploader(client);
		multipartObjectUploader.setPartSize(5 * MB);
		bucket = "testbucket";
		key = "1234567890";
	}
}
 