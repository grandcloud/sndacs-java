package com.snda.storage.core.support;
import static com.snda.storage.core.support.HttpDateTimeFormatter.formatDateTime;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.snda.storage.Location;
import com.snda.storage.SNDAServiceException;
import com.snda.storage.core.ListBucketCriteria;
import com.snda.storage.core.ListMultipartUploadsCriteria;
import com.snda.storage.policy.Policy;
import com.snda.storage.xml.CreateBucketConfiguration;
import com.snda.storage.xml.ListAllMyBucketsResult;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;
import com.snda.storage.xml.LocationConstraint;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class BucketOperationTest {

	@Mock
	private HttpInvoker invoker;

	private GenericStorageService client;
	private DateTime now;
	private String bucket;
	private String endpoint;
	private Location location;

	@Test
	public void testListBuckets() {
		ListAllMyBucketsResult expected = mock(ListAllMyBucketsResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				header("Date", formatDateTime(now)).
				build(), 
				ListAllMyBucketsResult.class)).
				thenReturn(expected);
		ListAllMyBucketsResult actual = client.listBuckets();
		assertSame(expected, actual);
	}
	
	@Test
	public void testCreateBucketWithPeferedLocation() {
		client.createBucket(bucket);
		
		verify(invoker).invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				header("Date", formatDateTime(now)).
				entity(new CreateBucketConfiguration(Location.HUABEI_1)).
				build(), 
				Void.class);
	}
	
	@Test
	public void testCreateBucketWithSpecifiedLocation() {
		CreateBucketConfiguration configuration = new CreateBucketConfiguration(Location.HUADONG_1);
		client.createBucket(bucket, configuration);
		
		verify(invoker).invoke(Request.builder().
				method(Method.PUT).
				endpoint("storage-huadong-1.grandcloud.cn").
				bucket(bucket).
				header("Date", formatDateTime(now)).
				entity(configuration).
				build(), 
				Void.class);
	}
	
	@Test
	public void testListObjects() {
		ListBucketResult expected = mock(ListBucketResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				header("Date", formatDateTime(now)).
				build(), 
				ListBucketResult.class)).thenReturn(expected);
		
		ListBucketResult actual = client.listObjects(bucket);
		assertSame(expected, actual);
	}
	
	@Test
	public void testListObjectsWithCriteria() {
		ListBucketResult expected = mock(ListBucketResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				parameter("delimiter", "/").
				parameter("prefix", "p").
				parameter("marker", "m").
				parameter("max-keys", 500).
				header("Date", formatDateTime(now)).
				build(), 
				ListBucketResult.class)).thenReturn(expected);
		
		ListBucketResult actual = client.listObjects(bucket, new ListBucketCriteria().
				withDelimiter("/").
				withMarker("m").
				withMaxKeys(500).
				withPrefix("p"));
		assertSame(expected, actual);
	}
	
	@Test
	public void testListMultipartUploads() {
		ListMultipartUploadsResult expected = mock(ListMultipartUploadsResult.class);
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				subResource("uploads").
				parameter("delimiter", "/").
				parameter("key-marker", "kkk").
				parameter("upload-id-marker", "1234567890").
				header("Date", formatDateTime(now)).
				build(), 
				ListMultipartUploadsResult.class)).thenReturn(expected);
		
		ListMultipartUploadsResult actual = client.listMultipartUploads(bucket, 
				new ListMultipartUploadsCriteria().
				withDelimiter("/").
				withKeyMarker("kkk").
				withUploadIdMarker("1234567890"));
		assertSame(expected, actual);
	}
	
	@Test
	public void testDoesBucketExist() {
		assertTrue(client.doesBucketExist(bucket));
	}
	
	@Test
	public void testDoesBucketExist2() {
		SNDAServiceException expectedException = mock(SNDAServiceException.class);
		when(expectedException.getStatus()).thenReturn(404);
		
		when(invoker.invoke(Request.builder().
				method(Method.HEAD).
				endpoint(endpoint).
				bucket(bucket).
				header("Date", formatDateTime(now)).
				build(), 
				Void.class)).thenThrow(expectedException);
		
		assertFalse(client.doesBucketExist(bucket));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testGetBucketLocation() {
		client = new GenericStorageService(invoker) {
			@Override
			protected DateTime now() {
				return now;
			}
		};
		
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				subResource("location").
				header("Date", formatDateTime(now)).
				build(), 
				LocationConstraint.class)).thenReturn(new LocationConstraint());
		Location expected = Location.DEFAULT;
		assertSame(expected, client.getBucketLocation(bucket));
		assertSame(expected, client.getBucketLocation(bucket));
		assertSame(expected, client.getBucketLocation(bucket));
		assertSame(expected, client.getBucketLocation(bucket));
		assertSame(expected, client.getBucketLocation(bucket));
		verify(invoker, only()).invoke(any(Request.class), any(Class.class));
	}
	
	@Test
	public void testSetBucketPolicy() {
		Policy policy = mock(Policy.class);
		client.setBucketPolicy(bucket, policy);
		
		verify(invoker).invoke(Request.builder().
				method(Method.PUT).
				endpoint(endpoint).
				bucket(bucket).
				subResource("policy").
				header("Date", formatDateTime(now)).
				entity(policy).
				build(), 
				Void.class);
	}
	
	@Test
	public void testGetBucketPolicy() {
		Policy expected = mock(Policy.class);
		
		when(invoker.invoke(Request.builder().
				method(Method.GET).
				endpoint(endpoint).
				bucket(bucket).
				subResource("policy").
				header("Date", formatDateTime(now)).
				build(),
				Policy.class)).thenReturn(expected);
		
		Policy actual = client.getBucketPolicy(bucket);
		assertSame(expected, actual);
	}
	
	@Test
	public void testDeleteBucketPolicy() {
		client.deleteBucketPolicy(bucket);
		
		verify(invoker).invoke(Request.builder().
				method(Method.DELETE).
				endpoint(endpoint).
				bucket(bucket).
				subResource("policy").
				header("Date", formatDateTime(now)).
				build(), 
				Void.class);
	}
	
	@Before
	public void setUp() {
		location = Location.HUABEI_1;
		endpoint = location.getEndpoint();
		now = new DateTime();
		bucket = "test-bucket";
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
