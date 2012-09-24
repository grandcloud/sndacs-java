package com.snda.storage.fluent.impl;

import org.joda.time.DateTime;

import com.snda.storage.core.CopyObjectRequest;
import com.snda.storage.core.CopySource;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.MetadataDirective;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.fluent.CopyObject;
import com.snda.storage.xml.CopyObjectResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class CopyObjectImpl implements CopyObject {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final CopyObjectRequest copyObjectRequest;
	
	public CopyObjectImpl(
			StorageService storageService, 
			String bucket, 
			String key) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
		this.copyObjectRequest = new CopyObjectRequest();
	}
	
	public CopyObjectImpl(
			StorageService storageService, 
			String bucket, 
			String key, 
			ObjectCreation objectCreation) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
		this.copyObjectRequest = new CopyObjectRequest().withObjectCreation(objectCreation);
	}

	@Override
	public CopyObjectResult copy() {
		return storageService.copyObject(bucket, key, copyObjectRequest);
	}

	@Override
	public CopyObject copySource(String bucket, String key) {
		copyObjectRequest.setCopySource(new CopySource(bucket, key));
		return this;
	}

	@Override
	public CopyObject copySourceIfModifiedSince(DateTime dateTime) {
		copyObjectRequest.getCopyCondition().setIfModifiedSince(dateTime);
		return this;
	}

	@Override
	public CopyObject copySourceIfUnmodifiedSince(DateTime dateTime) {
		copyObjectRequest.getCopyCondition().setIfUnmodifiedSince(dateTime);
		return this;
	}

	@Override
	public CopyObject copySourceIfMatch(String etag) {
		copyObjectRequest.getCopyCondition().setIfMatch(etag);
		return this;
	}

	@Override
	public CopyObject copySourceIfNoneMatch(String etag) {
		copyObjectRequest.getCopyCondition().setIfNoneMatch(etag);
		return this;
	}

	@Override
	public CopyObject copyMetadata() {
		copyObjectRequest.setMetadataDirective(MetadataDirective.COPY);
		return this;
	}

	@Override
	public CopyObject replaceMetadata() {
		copyObjectRequest.setMetadataDirective(MetadataDirective.REPLACE);
		return this;
	}
}
