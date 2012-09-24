package com.snda.storage.fluent.impl;

import org.joda.time.DateTime;

import com.snda.storage.core.CopyPartRequest;
import com.snda.storage.core.CopySource;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.Range;
import com.snda.storage.fluent.CopyPart;
import com.snda.storage.xml.CopyPartResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class CopyPartImpl implements CopyPart {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;
	private final int partNumber;
	private final CopyPartRequest copyPartRequest = new CopyPartRequest();
	
	public CopyPartImpl(
			StorageService storageService, 
			String bucket, 
			String key, 
			String uploadId, 
			int partNumber) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
		this.uploadId = uploadId;
		this.partNumber = partNumber;
	}

	@Override
	public CopyPartResult copy() {
		return storageService.copyPart(
				bucket, 
				key, 
				uploadId,
				partNumber,
				copyPartRequest);
	}

	@Override
	public CopyPart copySource(String bucket, String key) {
		copyPartRequest.setCopySource(new CopySource(bucket, key));
		return this;
	}

	@Override
	public CopyPart copySourceIfModifiedSince(DateTime dateTime) {
		copyPartRequest.getCopyCondition().setIfModifiedSince(dateTime);
		return this;
	}

	@Override
	public CopyPart copySourceIfUnmodifiedSince(DateTime dateTime) {
		copyPartRequest.getCopyCondition().setIfUnmodifiedSince(dateTime);
		return this;
	}

	@Override
	public CopyPart copySourceIfMatch(String etag) {
		copyPartRequest.getCopyCondition().setIfMatch(etag);
		return this;
	}

	@Override
	public CopyPart copySourceIfNoneMatch(String etag) {
		copyPartRequest.getCopyCondition().setIfNoneMatch(etag);
		return this;
	}

	@Override
	public CopyPart copySourceRange(long firstBytePosition) {
		copyPartRequest.setCopySourceRange(new Range(firstBytePosition));
		return this;
	}

	@Override
	public CopyPart copySourceRange(long firstBytePosition, long lastBytePosition) {
		copyPartRequest.setCopySourceRange(new Range(firstBytePosition, lastBytePosition));
		return this;
	}

}
