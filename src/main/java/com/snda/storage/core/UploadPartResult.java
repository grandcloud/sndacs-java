package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class UploadPartResult extends ValueObject {

	private final String eTag;

	public UploadPartResult(String eTag) {
		this.eTag = eTag;
	}

	public String getETag() {
		return eTag;
	}

}
