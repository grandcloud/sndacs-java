package com.snda.storage.fluent.impl;

import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.List;
import com.snda.storage.fluent.ListMultipartUploads;
import com.snda.storage.fluent.ListObjects;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class ListImpl implements List {

	private final StorageService storageService;
	private final String bucket;

	private String prefix;
	private String delimiter;

	public ListImpl(StorageService storageService, String bucket) {
		this.storageService = storageService;
		this.bucket = bucket;
	}

	@Override
	public List prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}

	@Override
	public List delimiter(String delimiter) {
		this.delimiter = delimiter;
		return this;
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
	public ListMultipartUploadsResult listMultipartUploads() {
		return newListMultipartUploads().listMultipartUploads();
	}

	@Override
	public ListBucketResult listObjects() {
		return newListObjects().listObjects();
	}

	private ListMultipartUploads newListMultipartUploads() {
		return new ListMultipartUploadsImpl(storageService, bucket).delimiter(delimiter).prefix(prefix);
	}

	private ListObjects newListObjects() {
		return new ListObjectsImpl(storageService, bucket).delimiter(delimiter).prefix(prefix);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getDelimiter() {
		return delimiter;
	}

}
