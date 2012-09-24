package com.snda.storage.fluent;

import com.snda.storage.xml.ListPartsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface ListParts {

	ListParts maxParts(int maxParts);
	
	ListParts partNumberMarker(int partNumberMarker);
	
	ListPartsResult listParts();
}
