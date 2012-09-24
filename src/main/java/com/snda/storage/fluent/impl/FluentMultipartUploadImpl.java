package com.snda.storage.fluent.impl;
import static com.google.common.base.Preconditions.*;
import java.util.List;

import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.CompleteMultipartUpload;
import com.snda.storage.fluent.FluentMultipartUpload;
import com.snda.storage.fluent.ListParts;
import com.snda.storage.fluent.PutPart;
import com.snda.storage.xml.ListPartsResult;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentMultipartUploadImpl implements FluentMultipartUpload {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final String uploadId;

	public FluentMultipartUploadImpl(StorageService storageService, String bucket, String key, String uploadId) {
		this.storageService = checkNotNull(storageService);
		this.bucket = checkNotNull(bucket);
		this.key = checkNotNull(key);
		this.uploadId = checkNotNull(uploadId);
	}

	@Override
	public ListParts maxParts(int maxParts) {
		return newListParts().maxParts(maxParts);
	}

	@Override
	public ListParts partNumberMarker(int partNumberMarker) {
		return newListParts().partNumberMarker(partNumberMarker);
	}

	@Override
	public ListPartsResult listParts() {
		return newListParts().listParts();
	}

	@Override
	public PutPart partNumber(int partNumber) {
		return newGenericPutPart(partNumber);
	}

	@Override
	public CompleteMultipartUpload part(Part part) {
		return newCompleteMultipartUpload().part(part);
	}

	@Override
	public CompleteMultipartUpload parts(List<Part> parts) {
		return newCompleteMultipartUpload().parts(parts);
	}

	@Override
	public void abort() {
		storageService.abortMultipartUpload(bucket, key, uploadId);
	}

	private PutPart newGenericPutPart(int partNumber) {
		return new PutPartImpl(storageService, bucket, key, uploadId, partNumber);
	}

	private ListParts newListParts() {
		return new ListPartsImpl(storageService, bucket, key, uploadId);
	}

	private CompleteMultipartUpload newCompleteMultipartUpload() {
		return new CompleteMultipartUploadImpl(storageService, bucket, key, uploadId);
	}
}
