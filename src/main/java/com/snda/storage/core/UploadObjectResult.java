package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class UploadObjectResult extends ValueObject {

	private final String eTag;

	public UploadObjectResult(String eTag) {
		this.eTag = eTag;
	}

	public String getETag() {
		return eTag;
	}

}
