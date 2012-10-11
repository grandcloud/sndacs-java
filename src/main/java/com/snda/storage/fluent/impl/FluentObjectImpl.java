package com.snda.storage.fluent.impl;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.CopyObject;
import com.snda.storage.fluent.FluentMultipartUpload;
import com.snda.storage.fluent.FluentObject;
import com.snda.storage.fluent.DownloadObject;
import com.snda.storage.fluent.PutObject;
import com.snda.storage.fluent.UploadObject;
import com.snda.storage.xml.InitiateMultipartUploadResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentObjectImpl implements FluentObject {

	private final StorageService storageService;
	private final String bucket;
	private final String key;

	public FluentObjectImpl(StorageService storageService, String bucket, String key) {
		this.storageService = checkNotNull(storageService);
		this.bucket = checkNotNull(bucket);
		this.key = checkNotNull(key);
	}

	@Override
	public DownloadObject range(long firstBytePosition) {
		return newGetObject().range(firstBytePosition);
	}

	@Override
	public DownloadObject range(long firstBytePosition, long lastBytePosition) {
		return newGetObject().range(firstBytePosition, lastBytePosition);
	}

	@Override
	public DownloadObject ifModifiedSince(DateTime dateTime) {
		return newGetObject().ifModifiedSince(dateTime);
	}

	@Override
	public DownloadObject ifUnmodifiedSince(DateTime dateTime) {
		return newGetObject().ifUnmodifiedSince(dateTime);
	}

	@Override
	public DownloadObject ifMatch(String etag) {
		return newGetObject().ifMatch(etag);
	}

	@Override
	public DownloadObject ifNoneMatch(String etag) {
		return newGetObject().ifNoneMatch(etag);
	}

	@Override
	public DownloadObject responseContentType(String responseContentType) {
		return newGetObject().responseContentType(responseContentType);
	}

	@Override
	public DownloadObject responseContentLanguage(String responseContentLanguage) {
		return newGetObject().responseContentLanguage(responseContentLanguage);
	}

	@Override
	public DownloadObject responseExpires(String responseExpires) {
		return newGetObject().responseExpires(responseExpires);
	}

	@Override
	public DownloadObject responseCacheControl(String responseCacheControl) {
		return newGetObject().responseCacheControl(responseCacheControl);
	}

	@Override
	public DownloadObject responseContentDisposition(String responseContentDisposition) {
		return newGetObject().responseContentDisposition(responseContentDisposition);
	}

	@Override
	public DownloadObject responseContentEncoding(String responseContentEncoding) {
		return newGetObject().responseContentEncoding(responseContentEncoding);
	}

	@Override
	public SNDAObject download() {
		return newGetObject().download();
	}

	@Override
	public SNDAObjectMetadata head() {
		return newGetObject().head();
	}

	@Override
	public PutObject reducedRedundancy() {
		return newPutObject().reducedRedundancy();
	}

	@Override
	public PutObject standardStorage() {
		return newPutObject().standardStorage();
	}

	@Override
	public PutObject expirationDays(int expirationDays) {
		return newPutObject().expirationDays(expirationDays);
	}

	@Override
	public PutObject metadata(String name, Object value) {
		return newPutObject().metadata(name, value);
	}

	@Override
	public PutObject metadata(Map<String, Object> metadata) {
		return newPutObject().metadata(metadata);
	}

	@Override
	public UploadObject contentMD5(String contentMD5) {
		return newPutObject().contentMD5(contentMD5);
	}

	@Override
	public PutObject contentType(String contentType) {
		return newPutObject().contentType(contentType);
	}

	@Override
	public PutObject cacheControl(String cacheControl) {
		return newPutObject().cacheControl(cacheControl);
	}

	@Override
	public PutObject contentDisposition(String contentDisposition) {
		return newPutObject().contentDisposition(contentDisposition);
	}

	@Override
	public PutObject contentEncoding(String contentEncoding) {
		return newPutObject().contentEncoding(contentEncoding);
	}

	@Override
	public PutObject expires(String expires) {
		return newPutObject().expires(expires);
	}

	@Override
	public InitiateMultipartUploadResult initiateMultipartUpload() {
		return newPutObject().initiateMultipartUpload();
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
	public UploadObject multipartUploadSize(long multipartUploadSize) {
		return newPutObject().multipartUploadSize(multipartUploadSize);
	}

	@Override
	public UploadObject partSize(long partSize) {
		return newPutObject().partSize(partSize);
	}

	@Override
	public UploadObject entity(Entity entity) {
		return newPutObject().entity(entity);
	}

	@Override
	public UploadObject entity(File file) {
		return newPutObject().entity(file);
	}

	@Override
	public UploadObject entity(long contentLength, InputStream inputStream) {
		return newPutObject().entity(contentLength, inputStream);
	}

	@Override
	public UploadObject entity(long contentLength, InputSupplier<? extends InputStream> supplier) {
		return newPutObject().entity(contentLength, supplier);
	}

	@Override
	public void update() {
		newPutObject().update();
	}

	@Override
	public void delete() {
		storageService.deleteObject(bucket, key);
	}

	@Override
	public FluentMultipartUpload multipartUpload(String uploadId) {
		return new FluentMultipartUploadImpl(storageService, bucket, key, uploadId);
	}

	private DownloadObject newGetObject() {
		return new GetObjectImpl(storageService, bucket, key);
	}

	private PutObject newPutObject() {
		return new PutObjectImpl(storageService, bucket, key);
	}
	
	private CopyObject newCopyObject() {
		return new CopyObjectImpl(storageService, bucket, key);
	}
}
