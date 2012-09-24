package com.snda.storage.fluent.impl;

import java.io.File;
import java.io.InputStream;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.UploadPartRequest;
import com.snda.storage.core.UploadPartResult;
import com.snda.storage.core.support.FileEntity;
import com.snda.storage.core.support.InputStreamEntity;
import com.snda.storage.core.support.InputSupplierEntity;
import com.snda.storage.fluent.UploadPart;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class UploadPartImpl implements UploadPart {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;
	private final int partNumber;
	private final UploadPartRequest uploadPartRequest = new UploadPartRequest();
	
	public UploadPartImpl(
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
	public UploadPartResult upload() {
		return storageService.uploadPart(
				bucket,
				key, 
				uploadId, 
				partNumber,
				uploadPartRequest);
	}

	@Override
	public UploadPart contentMD5(String contentMD5) {
		uploadPartRequest.setContentMD5(contentMD5);
		return this;
	}

	@Override
	public UploadPart entity(Entity entity) {
		uploadPartRequest.setEntity(entity);
		return this;
	}

	@Override
	public UploadPart entity(File file) {
		uploadPartRequest.setEntity(new FileEntity(file));
		return this;
	}

	@Override
	public UploadPart entity(long contentLength, InputStream inputStream) {
		uploadPartRequest.setEntity(new InputStreamEntity(contentLength, inputStream));
		return this;
	}

	@Override
	public UploadPart entity(long contentLength, InputSupplier<? extends InputStream> supplier) {
		uploadPartRequest.setEntity(new InputSupplierEntity(contentLength, supplier));
		return this;
	}

}
