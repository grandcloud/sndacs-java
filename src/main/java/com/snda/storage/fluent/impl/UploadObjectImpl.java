package com.snda.storage.fluent.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.StorageClass;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;
import com.snda.storage.core.support.FileEntity;
import com.snda.storage.core.support.InputStreamEntity;
import com.snda.storage.core.support.InputSupplierEntity;
import com.snda.storage.core.support.MultipartObjectUploader;
import com.snda.storage.core.support.ObjectUploader;
import com.snda.storage.fluent.UploadObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class UploadObjectImpl implements UploadObject {

	private final String bucket;
	private final String key;
	private final ObjectUploader objectUploader;
	private final UploadObjectRequest uploadObjectRequest;
	
	public UploadObjectImpl(
			StorageService storageService, 
			String bucket, 
			String key, 
			ObjectCreation objectCreation) {
		this.bucket = bucket;
		this.key = key;
		this.objectUploader = new MultipartObjectUploader(storageService);
		this.uploadObjectRequest = new UploadObjectRequest().withObjectCreation(objectCreation);
	}

	@Override
	public UploadObjectResult upload() {
		return objectUploader.putObject(bucket, key, uploadObjectRequest);
	}

	@Override
	public UploadObject multipartUploadSize(long multipartUploadSize) {
		objectUploader.setMultipartUploadSize(multipartUploadSize);
		return this;
	}

	@Override
	public UploadObject partSize(long partSize) {
		objectUploader.setPartSize(partSize);
		return this;
	}

	@Override
	public UploadObject entity(Entity entity) {
		uploadObjectRequest.setEntity(entity);
		return this;
	}

	@Override
	public UploadObject entity(File file) {
		uploadObjectRequest.setEntity(new FileEntity(file));
		return this;
	}

	@Override
	public UploadObject entity(long contentLength, InputStream inputStream) {
		uploadObjectRequest.setEntity(new InputStreamEntity(contentLength, inputStream));
		return this;
	}

	@Override
	public UploadObject entity(long contentLength, InputSupplier<? extends InputStream> supplier) {
		uploadObjectRequest.setEntity(new InputSupplierEntity(contentLength, supplier));
		return this;
	}

	@Override
	public UploadObject reducedRedundancy() {
		uploadObjectRequest.getObjectCreation().setStorageClass(StorageClass.REDUCED_REDUNDANCY);
		return this;
	}

	@Override
	public UploadObject standardStorage() {
		uploadObjectRequest.getObjectCreation().setStorageClass(StorageClass.STANDARD);
		return this;
	}

	@Override
	public UploadObject expirationDays(int expirationDays) {
		uploadObjectRequest.getObjectCreation().setExpirationDays(expirationDays);
		return this;
	}

	@Override
	public UploadObject metadata(String name, Object value) {
		uploadObjectRequest.getObjectCreation().getMetadata().put(name, value);
		return this;
	}

	@Override
	public UploadObject metadata(Map<String, Object> metadata) {
		uploadObjectRequest.getObjectCreation().getMetadata().putAll(metadata);
		return this;
	}

	@Override
	public UploadObject contentMD5(String contentMD5) {
		uploadObjectRequest.setContentMD5(contentMD5);
		return this;
	}

	@Override
	public UploadObject contentType(String contentType) {
		uploadObjectRequest.getObjectCreation().setContentType(contentType);
		return this;
	}

	@Override
	public UploadObject cacheControl(String cacheControl) {
		uploadObjectRequest.getObjectCreation().setCacheControl(cacheControl);
		return this;
	}

	@Override
	public UploadObject contentDisposition(String contentDisposition) {
		uploadObjectRequest.getObjectCreation().setContentDisposition(contentDisposition);
		return this;
	}

	@Override
	public UploadObject contentEncoding(String contentEncoding) {
		uploadObjectRequest.getObjectCreation().setContentEncoding(contentEncoding);
		return this;
	}

	@Override
	public UploadObject expires(String expires) {
		uploadObjectRequest.getObjectCreation().setExpires(expires);
		return this;
	}
}
