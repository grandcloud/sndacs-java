package com.snda.storage.fluent;

import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface List {

	List prefix(String prefix);

	List delimiter(String delimiter);

	ListMultipartUploads keyMarker(String keyMarker);

	ListMultipartUploads uploadIdMarker(String uploadIdMarker);

	ListMultipartUploads maxUploads(int maxUploads);

	ListMultipartUploadsResult listMultipartUploads();

	ListObjects marker(String marker);

	ListObjects maxKeys(int maxKeys);

	ListBucketResult listObjects();
}
