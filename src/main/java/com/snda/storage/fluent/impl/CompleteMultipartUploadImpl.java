package com.snda.storage.fluent.impl;

import java.util.List;

import com.google.common.collect.Lists;
import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.CompleteMultipartUpload;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class CompleteMultipartUploadImpl implements CompleteMultipartUpload {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;
	private final List<Part> parts = Lists.newArrayList();

	public CompleteMultipartUploadImpl(
			StorageService storageService, 
			String bucket, 
			String key, 
			String uploadId) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
		this.uploadId = uploadId;
	}

	@Override
	public CompleteMultipartUpload part(Part part) {
		parts.add(part);
		return this;
	}

	@Override
	public CompleteMultipartUpload parts(List<Part> parts) {
		parts.addAll(parts);
		return this;
	}

	@Override
	public CompleteMultipartUploadResult complete() {
		return storageService.completeMultipartUpload(
				bucket,
				key,
				uploadId,
				parts);
	}

}
