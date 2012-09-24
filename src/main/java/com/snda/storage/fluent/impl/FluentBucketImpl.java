package com.snda.storage.fluent.impl;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.UUID;

import com.snda.storage.Location;
import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.Create;
import com.snda.storage.fluent.FluentBucket;
import com.snda.storage.fluent.FluentObject;
import com.snda.storage.fluent.Get;
import com.snda.storage.fluent.GetDeletePolicy;
import com.snda.storage.fluent.List;
import com.snda.storage.fluent.ListMultipartUploads;
import com.snda.storage.fluent.ListObjects;
import com.snda.storage.fluent.SetPolicy;
import com.snda.storage.policy.Policy;
import com.snda.storage.xml.CreateBucketConfiguration;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentBucketImpl implements FluentBucket {

	private final StorageService storageService;
	private final String bucket;

	public FluentBucketImpl(StorageService storageService, String bucket) {
		this.storageService = checkNotNull(storageService);
		this.bucket = checkNotNull(bucket);
	}

	@Override
	public List prefix(String prefix) {
		return newList().prefix(prefix);
	}

	@Override
	public List delimiter(String delimiter) {
		return newList().delimiter(delimiter);
	}

	@Override
	public ListMultipartUploadsResult listMultipartUploads() {
		return newListMultipartUploads().listMultipartUploads();
	}

	@Override
	public ListBucketResult listObjects() {
		return newListObjects().listObjects();
	}

	@Override
	public ListMultipartUploads keyMarker(String keyMarker) {
		return newListMultipartUploads().keyMarker(keyMarker);
	}

	@Override
	public ListMultipartUploads uploadIdMarker(String uploadIdMarker) {
		return newListMultipartUploads().uploadIdMarker(uploadIdMarker);
	}

	@Override
	public ListMultipartUploads maxUploads(int maxUploads) {
		return newListMultipartUploads().maxUploads(maxUploads);
	}

	@Override
	public ListObjects marker(String marker) {
		return newListObjects().marker(marker);
	}

	@Override
	public ListObjects maxKeys(int maxKeys) {
		return newListObjects().maxKeys(maxKeys);
	}

	@Override
	public FluentObject object(String key) {
		return newObjectOperation(key);
	}

	@Override
	public void create() {
		storageService.createBucket(bucket);
	}

	@Override
	public boolean exist() {
		return storageService.doesBucketExist(bucket);
	}

	@Override
	public void delete() {
		storageService.deleteBucket(bucket);
	}

	@Override
	public Create location(final Location location) {
		return new Create() {
			@Override
			public void create() {
				storageService.createBucket(bucket, new CreateBucketConfiguration(location));
			}
		};
	}

	@Override
	public Get<Location> location() {
		return new Get<Location>() {
			@Override
			public Location get() {
				return storageService.getBucketLocation(bucket);
			}
		};
	}

	@Override
	public GetDeletePolicy policy() {
		return new GetDeletePolicy() {
			@Override
			public Policy get() {
				return storageService.getBucketPolicy(bucket);
			}

			@Override
			public void delete() {
				storageService.deleteBucketPolicy(bucket);
			}
		};
	}

	@Override
	public SetPolicy policy(final Policy policy) {
		return new SetPolicy() {
			@Override
			public void set() {
				storageService.setBucketPolicy(bucket, policy);
			}
		};
	}

	private ListObjects newListObjects() {
		return new ListObjectsImpl(storageService, bucket);
	}

	private ListMultipartUploads newListMultipartUploads() {
		return new ListMultipartUploadsImpl(storageService, bucket);
	}

	private FluentObject newObjectOperation(String key) {
		return new FluentObjectImpl(storageService, bucket, key);
	}

	private List newList() {
		return new ListImpl(storageService, bucket);
	}

	protected String generatePolicyId() {
		return UUID.randomUUID().toString();
	}
}
