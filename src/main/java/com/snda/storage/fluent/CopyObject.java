package com.snda.storage.fluent;

import org.joda.time.DateTime;

import com.snda.storage.xml.CopyObjectResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface CopyObject extends Copy<CopyObjectResult> {

	CopyObject copySource(String bucket, String key);

	CopyObject copySourceIfModifiedSince(DateTime dateTime);

	CopyObject copySourceIfUnmodifiedSince(DateTime dateTime);

	CopyObject copySourceIfMatch(String etag);

	CopyObject copySourceIfNoneMatch(String etag);

	CopyObject copyMetadata();

	CopyObject replaceMetadata();

}
