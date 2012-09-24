package com.snda.storage.fluent;

import com.snda.storage.xml.ListBucketResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface ListObjects {

	ListObjects prefix(String prefix);

	ListObjects delimiter(String delimiter);
	
	ListObjects marker(String marker);
	
	ListObjects maxKeys(int maxKeys);
	
	ListBucketResult listObjects();
	
}
