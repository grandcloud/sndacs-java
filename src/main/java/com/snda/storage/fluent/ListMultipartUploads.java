package com.snda.storage.fluent;

import com.snda.storage.xml.ListMultipartUploadsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface ListMultipartUploads {

	ListMultipartUploads prefix(String prefix);

	ListMultipartUploads delimiter(String delimiter);
	
	ListMultipartUploads keyMarker(String keyMarker);

	ListMultipartUploads uploadIdMarker(String uploadIdMarker);

	ListMultipartUploads maxUploads(int maxUploads);

	ListMultipartUploadsResult listMultipartUploads();
}
