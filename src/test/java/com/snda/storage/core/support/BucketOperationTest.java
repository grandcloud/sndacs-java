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
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withHeader("Date", formatDateTime(now)), 
				ListAllMyBucketsResult.class)).
				thenReturn(expected);
		ListAllMyBucketsResult actual = client.listBuckets();
		assertSame(expected, actual);
	}
	
	@Test
	public void testCreateBucketWithPeferedLocation() {
		client.createBucket(bucket);
		
		verify(invoker).invoke(new Request().
				withMethod(Method.PUT).
				withEndpoint(endpoint).
				withBucket(bucket).
				withHeader("Date", formatDateTime(now)).
				withEntity(new CreateBucketConfiguration(Location.HUABEI_1)), 
				Void.class);
	}
	
	@Test
	public void testCreateBucketWithSpecifiedLocation() {
		CreateBucketConfiguration configuration = new CreateBucketConfiguration(Location.HUADONG_1);
		client.createBucket(bucket, configuration);
		
		verify(invoker).invoke(new Request().
				withMethod(Method.PUT).
				withEndpoint("storage-huadong-1.grandcloud.cn").
				withBucket(bucket).
				withHeader("Date", formatDateTime(now)).
				withEntity(configuration), 
				Void.class);
	}
	
	@Test
	public void testListObjects() {
		ListBucketResult expected = mock(ListBucketResult.class);
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withBucket(bucket).
				withHeader("Date", formatDateTime(now)), 
				ListBucketResult.class)).thenReturn(expected);
		
		ListBucketResult actual = client.listObjects(bucket);
		assertSame(expected, actual);
	}
	
	@Test
	public void testListObjectsWithCriteria() {
		ListBucketResult expected = mock(ListBucketResult.class);
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withBucket(bucket).
				withParameter("delimiter", "/").
				withParameter("marker", "m").
				withParameter("max-keys", 500).
				withParameter("prefix", "p").
				withHeader("Date", formatDateTime(now)), 
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
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withBucket(bucket).
				withSubResource("uploads").
				withParameter("delimiter", "/").
				withParameter("key-marker", "kkk").
				withParameter("upload-id-marker", "1234567890").
				withHeader("Date", formatDateTime(now)), 
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
		
		when(invoker.invoke(new Request().
				withMethod(Method.HEAD).
				withEndpoint(endpoint).
				withBucket(bucket).
				withHeader("Date", formatDateTime(now)), 
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
		
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withBucket(bucket).
				withSubResource("location").
				withHeader("Date", formatDateTime(now)), 
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
		
		verify(invoker).invoke(new Request().
				withMethod(Method.PUT).
				withEndpoint(endpoint).
				withBucket(bucket).
				withSubResource("policy").
				withHeader("Date", formatDateTime(now)).
				withEntity(policy), 
				Void.class);
	}
	
	@Test
	public void testGetBucketPolicy() {
		Policy expected = mock(Policy.class);
		
		when(invoker.invoke(new Request().
				withMethod(Method.GET).
				withEndpoint(endpoint).
				withBucket(bucket).
				withSubResource("policy").
				withHeader("Date", formatDateTime(now)),
				Policy.class)).thenReturn(expected);
		
		Policy actual = client.getBucketPolicy(bucket);
		assertSame(expected, actual);
	}
	
	@Test
	public void testDeleteBucketPolicy() {
		client.deleteBucketPolicy(bucket);
		
		verify(invoker).invoke(new Request().
				withMethod(Method.DELETE).
				withEndpoint(endpoint).
				withBucket(bucket).
				withSubResource("policy").
				withHeader("Date", formatDateTime(now)), 
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
