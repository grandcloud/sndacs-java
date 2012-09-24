package com.snda.storage.fluent.impl;

import org.joda.time.DateTime;

import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.core.GetObjectRequest;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.Range;
import com.snda.storage.fluent.DownloadObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class GetObjectImpl implements DownloadObject {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final GetObjectRequest getObjectRequest = new GetObjectRequest();
	
	public GetObjectImpl(StorageService storageService, String bucket, String key) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
	}

	@Override
	public SNDAObjectMetadata head() {
		return storageService.headObject(bucket, key, getObjectRequest);
	}

	@Override
	public SNDAObject download() {
		return storageService.downloadObject(bucket, key, getObjectRequest);
	}

	@Override
	public DownloadObject range(long firstBytePosition) {
		getObjectRequest.setRange(new Range(firstBytePosition));
		return this;
	}

	@Override
	public DownloadObject range(long firstBytePosition, long lastBytePosition) {
		getObjectRequest.setRange(new Range(firstBytePosition, lastBytePosition));
		return this;
	}

	@Override
	public DownloadObject ifModifiedSince(DateTime dateTime) {
		getObjectRequest.getCondition().setIfModifiedSince(dateTime);
		return this;
	}

	@Override
	public DownloadObject ifUnmodifiedSince(DateTime dateTime) {
		getObjectRequest.getCondition().setIfUnmodifiedSince(dateTime);
		return this;
	}

	@Override
	public DownloadObject ifMatch(String etag) {
		getObjectRequest.getCondition().setIfMatch(etag);
		return this;
	}

	@Override
	public DownloadObject ifNoneMatch(String etag) {
		getObjectRequest.getCondition().setIfNoneMatch(etag);
		return this;
	}

	@Override
	public DownloadObject responseContentType(String responseContentType) {
		getObjectRequest.getResponseOverride().setContentType(responseContentType);
		return this;
	}

	@Override
	public DownloadObject responseContentLanguage(String responseContentLanguage) {
		getObjectRequest.getResponseOverride().setContentLanguage(responseContentLanguage);
		return this;
	}

	@Override
	public DownloadObject responseExpires(String responseExpires) {
		getObjectRequest.getResponseOverride().setExpires(responseExpires);
		return this;
	}

	@Override
	public DownloadObject responseCacheControl(String responseCacheControl) {
		getObjectRequest.getResponseOverride().setCacheControl(responseCacheControl);
		return this;
	}

	@Override
	public DownloadObject responseContentDisposition(String responseContentDisposition) {
		getObjectRequest.getResponseOverride().setContentDisposition(responseContentDisposition);
		return this;
	}

	@Override
	public DownloadObject responseContentEncoding(String responseContentEncoding) {
		getObjectRequest.getResponseOverride().setContentEncoding(responseContentEncoding);
		return this;
	}
	
}
