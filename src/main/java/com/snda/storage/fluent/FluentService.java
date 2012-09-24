package com.snda.storage.fluent;

import com.snda.storage.xml.ListAllMyBucketsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentService {

	ListAllMyBucketsResult listBuckets();
	
	FluentBucket bucket(String name);
}
