package com.snda.storage;

import com.snda.storage.fluent.FluentBucket;
import com.snda.storage.xml.ListAllMyBucketsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface SNDAStorage {

	ListAllMyBucketsResult listBuckets();

	FluentBucket bucket(String name);

	PresignedURIBuilder presignedURIBuilder();
	
	void destory();
}
