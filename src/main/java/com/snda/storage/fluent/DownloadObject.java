package com.snda.storage.fluent;

import org.joda.time.DateTime;

import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface DownloadObject {

	SNDAObject download();
	
	SNDAObjectMetadata head();
	
	DownloadObject range(long firstBytePosition);

	DownloadObject range(long firstBytePosition, long lastBytePosition);

	DownloadObject ifModifiedSince(DateTime dateTime);
	
	DownloadObject ifUnmodifiedSince(DateTime dateTime);
	
	DownloadObject ifMatch(String etag);
	
	DownloadObject ifNoneMatch(String etag);
	
	DownloadObject responseContentType(String responseContentType);

	DownloadObject responseContentLanguage(String responseContentLanguage);

	DownloadObject responseExpires(String responseExpires);

	DownloadObject responseCacheControl(String responseCacheControl);

	DownloadObject responseContentDisposition(String responseContentDisposition);

	DownloadObject responseContentEncoding(String responseContentEncoding);
}
