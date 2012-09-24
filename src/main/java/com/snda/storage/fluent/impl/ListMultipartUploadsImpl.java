package com.snda.storage.fluent.impl;

import com.snda.storage.core.StorageService;
import com.snda.storage.core.ListMultipartUploadsCriteria;
import com.snda.storage.fluent.ListMultipartUploads;
import com.snda.storage.xml.ListMultipartUploadsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class ListMultipartUploadsImpl implements ListMultipartUploads {
	
	private final StorageService storageService;
	private final String bucket;
	private final ListMultipartUploadsCriteria criteria = new ListMultipartUploadsCriteria();
	
	public ListMultipartUploadsImpl(StorageService storageService, String bucket) {
		this.storageService = storageService;
		this.bucket = bucket;
	}

	@Override
	public ListMultipartUploads prefix(String prefix) {
		criteria.setPrefix(prefix);
		return this;
	}

	@Override
	public ListMultipartUploads delimiter(String delimiter) {
		criteria.setDelimiter(delimiter);
		return this;
	}

	@Override
	public ListMultipartUploads keyMarker(String keyMarker) {
		criteria.setKeyMarker(keyMarker);
		return this;
	}

	@Override
	public ListMultipartUploads uploadIdMarker(String uploadIdMarker) {
		criteria.setUploadIdMarker(uploadIdMarker);
		return this;
	}

	@Override
	public ListMultipartUploads maxUploads(int maxUploads) {
		criteria.setMaxUploads(maxUploads);
		return this;
	}

	@Override
	public ListMultipartUploadsResult listMultipartUploads() {
		return storageService.listMultipartUploads(bucket, criteria);
	}

}
