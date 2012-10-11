package com.snda.storage.fluent.impl;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.StorageClass;
import com.snda.storage.core.CopyObjectRequest;
import com.snda.storage.core.CopySource;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.MetadataDirective;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.fluent.CopyObject;
import com.snda.storage.fluent.PutObject;
import com.snda.storage.fluent.UploadObject;
import com.snda.storage.xml.InitiateMultipartUploadResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class PutObjectImpl implements PutObject {

	private final StorageService storageService;
	private final String bucket;
	private final String key;
	private final ObjectCreation objectCreation = new ObjectCreation();

	public PutObjectImpl(StorageService storageService, String bucket, String key) {
		this.storageService = storageService;
		this.bucket = bucket;
		this.key = key;
	}

	@Override
	public UploadObject contentMD5(String contentMD5) {
		return newUploadObject().contentMD5(contentMD5);
	}
	
	@Override
	public UploadObject multipartUploadSize(long multipartUploadSize) {
		return newUploadObject().multipartUploadSize(multipartUploadSize);
	}

	@Override
	public UploadObject partSize(long partSize) {
		return newUploadObject().partSize(partSize);
	}

	@Override
	public UploadObject entity(Entity entity) {
		return newUploadObject().entity(entity);
	}

	@Override
	public UploadObject entity(File file) {
		return newUploadObject().entity(file);
	}

	@Override
	public UploadObject entity(final long contentLength, final InputStream inputStream) {
		return newUploadObject().entity(contentLength, inputStream);
	}

	@Override
	public UploadObject entity(long contentLength, InputSupplier<? extends InputStream> supplier) {
		return newUploadObject().entity(contentLength, supplier);
	}

	@Override
	public PutObject reducedRedundancy() {
		objectCreation.setStorageClass(StorageClass.REDUCED_REDUNDANCY);
		return this;
	}

	@Override
	public PutObject standardStorage() {
		objectCreation.setStorageClass(StorageClass.STANDARD);
		return this;
	}

	@Override
	public PutObject expirationDays(int expirationDays) {
		objectCreation.setExpirationDays(expirationDays);
		return this;
	}

	@Override
	public PutObject metadata(String name, Object value) {
		objectCreation.getMetadata().put(name, value);
		return this;
	}

	@Override
	public PutObject metadata(Map<String, Object> metadata) {
		objectCreation.getMetadata().putAll(metadata);
		return null;
	}

	@Override
	public PutObject contentType(String contentType) {
		objectCreation.setContentType(contentType);
		return this;
	}

	@Override
	public PutObject cacheControl(String cacheControl) {
		objectCreation.setCacheControl(cacheControl);
		return this;
	}

	@Override
	public PutObject contentDisposition(String contentDisposition) {
		objectCreation.setContentDisposition(contentDisposition);
		return this;
	}

	@Override
	public PutObject contentEncoding(String contentEncoding) {
		objectCreation.setContentEncoding(contentEncoding);
		return this;
	}

	@Override
	public PutObject expires(String expires) {
		objectCreation.setExpires(expires);
		return this;
	}

	@Override
	public CopyObject copySource(String bucket, String key) {
		return newCopyObject().copySource(bucket, key);
	}

	@Override
	public CopyObject copySourceIfModifiedSince(DateTime dateTime) {
		return newCopyObject().copySourceIfModifiedSince(dateTime);
	}

	@Override
	public CopyObject copySourceIfUnmodifiedSince(DateTime dateTime) {
		return newCopyObject().copySourceIfUnmodifiedSince(dateTime);
	}

	@Override
	public CopyObject copySourceIfMatch(String etag) {
		return newCopyObject().copySourceIfMatch(etag);
	}

	@Override
	public CopyObject copySourceIfNoneMatch(String etag) {
		return newCopyObject().copySourceIfNoneMatch(etag);
	}

	@Override
	public CopyObject copyMetadata() {
		return newCopyObject().copyMetadata();
	}

	@Override
	public CopyObject replaceMetadata() {
		return newCopyObject().replaceMetadata();
	}

	@Override
	public InitiateMultipartUploadResult initiateMultipartUpload() {
		return storageService.initiateMultipartUpload(bucket, key, objectCreation);
	}

	@Override
	public void update() {
		storageService.copyObject(bucket, key, new CopyObjectRequest().
				withCopySource(new CopySource(bucket, key)).
				withObjectCreation(objectCreation).
				withMetadataDirective(MetadataDirective.REPLACE));
	}

	private CopyObject newCopyObject() {
		return new CopyObjectImpl(storageService, bucket, key, objectCreation);
	}

	private UploadObject newUploadObject() {
		return new UploadObjectImpl(storageService, bucket, key, objectCreation);
	}

}
