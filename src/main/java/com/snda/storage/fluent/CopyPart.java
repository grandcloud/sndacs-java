package com.snda.storage.fluent;

import org.joda.time.DateTime;

import com.snda.storage.xml.CopyPartResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface CopyPart extends Copy<CopyPartResult> {

	CopyPart copySource(String bucket, String key);

	CopyPart copySourceIfModifiedSince(DateTime dateTime);

	CopyPart copySourceIfUnmodifiedSince(DateTime dateTime);

	CopyPart copySourceIfMatch(String etag);

	CopyPart copySourceIfNoneMatch(String etag);

	CopyPart copySourceRange(long firstBytePosition);

	CopyPart copySourceRange(long firstBytePosition, long lastBytePosition);

}
