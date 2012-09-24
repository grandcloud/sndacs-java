package com.snda.storage.fluent.impl;

import com.snda.storage.core.StorageService;
import com.snda.storage.core.ListBucketCriteria;
import com.snda.storage.fluent.ListObjects;
import com.snda.storage.xml.ListBucketResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class ListObjectsImpl implements ListObjects {

	private final StorageService storageService;
	private final String bucket;
	private final ListBucketCriteria criteria = new ListBucketCriteria();
	
	public ListObjectsImpl(StorageService storageService, String bucket) {
		this.storageService = storageService;
		this.bucket = bucket;
	}

	@Override
	public ListObjects prefix(String prefix) {
		criteria.setPrefix(prefix);
		return this;
	}

	@Override
	public ListObjects delimiter(String delimiter) {
		criteria.setDelimiter(delimiter);
		return this;
	}

	@Override
	public ListObjects marker(String marker) {
		criteria.setMarker(marker);
		return this;
	}

	@Override
	public ListObjects maxKeys(int maxKeys) {
		criteria.setMaxKeys(maxKeys);
		return this;
	}

	@Override
	public ListBucketResult listObjects() {
		return storageService.listObjects(bucket, criteria);
	}

}
