package com.snda.storage.fluent.impl;

import com.snda.storage.core.StorageService;
import com.snda.storage.core.ListPartsCriteria;
import com.snda.storage.fluent.ListParts;
import com.snda.storage.xml.ListPartsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class ListPartsImpl implements ListParts {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;
	private final ListPartsCriteria criteria = new ListPartsCriteria();
	
	public ListPartsImpl(StorageService storageService, String bucket, String key, String uploadId) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
		this.uploadId = uploadId;
	}

	@Override
	public ListParts maxParts(int maxParts) {
		criteria.setMaxParts(maxParts);
		return this;
	}

	@Override
	public ListParts partNumberMarker(int partNumberMarker) {
		criteria.setPartNumberMarker(partNumberMarker);
		return this;
	}

	@Override
	public ListPartsResult listParts() {
		return storageService.listParts(bucket, key, uploadId, criteria);
	}

}
