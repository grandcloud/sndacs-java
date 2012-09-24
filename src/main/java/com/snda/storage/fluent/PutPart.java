package com.snda.storage.fluent;

import java.io.File;
import java.io.InputStream;

import org.joda.time.DateTime;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface PutPart {

	UploadPart contentMD5(String contentMD5);

	UploadPart entity(Entity entity);

	UploadPart entity(File file);

	UploadPart entity(long contentLength, InputStream inputStream);

	UploadPart entity(long contentLength, InputSupplier<? extends InputStream> supplier);

	CopyPart copySource(String bucket, String key);

	CopyPart copySourceIfModifiedSince(DateTime dateTime);

	CopyPart copySourceIfUnmodifiedSince(DateTime dateTime);

	CopyPart copySourceIfMatch(String etag);

	CopyPart copySourceIfNoneMatch(String etag);

	CopyPart copySourceRange(long firstBytePosition);

	CopyPart copySourceRange(long firstBytePosition, long lastBytePosition);

}
