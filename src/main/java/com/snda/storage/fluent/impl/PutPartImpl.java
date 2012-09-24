package com.snda.storage.fluent.impl;

import java.io.File;
import java.io.InputStream;

import org.joda.time.DateTime;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.CopyPart;
import com.snda.storage.fluent.PutPart;
import com.snda.storage.fluent.UploadPart;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class PutPartImpl implements PutPart {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;
	private final int partNumber;

	public PutPartImpl(
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
	public UploadPart contentMD5(String contentMD5) {
		return newUploadPart().contentMD5(contentMD5);
	}

	@Override
	public UploadPart entity(Entity entity) {
		return newUploadPart().entity(entity);
	}

	@Override
	public UploadPart entity(File file) {
		return newUploadPart().entity(file);
	}

	@Override
	public UploadPart entity(long contentLength, InputStream inputStream) {
		return newUploadPart().entity(contentLength, inputStream);
	}

	@Override
	public UploadPart entity(long contentLength, InputSupplier<? extends InputStream> supplier) {
		return newUploadPart().entity(contentLength, supplier);
	}

	@Override
	public CopyPart copySource(String bucket, String key) {
		return newCopyPart().copySource(bucket, key);
	}

	@Override
	public CopyPart copySourceIfModifiedSince(DateTime dateTime) {
		return newCopyPart().copySourceIfModifiedSince(dateTime);
	}

	@Override
	public CopyPart copySourceIfUnmodifiedSince(DateTime dateTime) {
		return newCopyPart().copySourceIfUnmodifiedSince(dateTime);
	}

	@Override
	public CopyPart copySourceIfMatch(String etag) {
		return newCopyPart().copySourceIfMatch(etag);
	}

	@Override
	public CopyPart copySourceIfNoneMatch(String etag) {
		return newCopyPart().copySourceIfNoneMatch(etag);
	}

	@Override
	public CopyPart copySourceRange(long firstBytePosition) {
		return newCopyPart().copySourceRange(firstBytePosition);
	}

	@Override
	public CopyPart copySourceRange(long firstBytePosition, long lastBytePosition) {
		return newCopyPart().copySourceRange(firstBytePosition, lastBytePosition);
	}

	private UploadPart newUploadPart() {
		return new UploadPartImpl(storageService, bucket, key, uploadId, partNumber);
	}
	
	private CopyPart newCopyPart() {
		return new CopyPartImpl(storageService, bucket, key, uploadId, partNumber);
	}
}
