package com.snda.storage.fluent;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.xml.InitiateMultipartUploadResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface PutObject {

	InitiateMultipartUploadResult initiateMultipartUpload();

	PutObject reducedRedundancy();
	
	PutObject standardStorage();

	PutObject expirationDays(int expirationDays);

	PutObject metadata(String name, Object value);
	
	PutObject metadata(Map<String, Object> metadata);

	PutObject contentType(String contentType);

	PutObject cacheControl(String cacheControl);

	PutObject contentDisposition(String contentDisposition);

	PutObject contentEncoding(String contentEncoding);

	PutObject expires(String expires);

	void update();
	
	UploadObject contentMD5(String contentMD5);

	UploadObject partSize(long partSize);

	UploadObject entity(Entity entity);
	
	UploadObject entity(File file);

	UploadObject entity(long contentLength, InputStream inputStream);

	UploadObject entity(long contentLength, InputSupplier<? extends InputStream> supplier);

	CopyObject copySource(String bucket, String key);

	CopyObject copySourceIfModifiedSince(DateTime dateTime);

	CopyObject copySourceIfUnmodifiedSince(DateTime dateTime);

	CopyObject copySourceIfMatch(String etag);

	CopyObject copySourceIfNoneMatch(String etag);

	CopyObject copyMetadata();

	CopyObject replaceMetadata();

}
