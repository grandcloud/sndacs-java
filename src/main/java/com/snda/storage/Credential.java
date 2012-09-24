package com.snda.storage;

import com.snda.storage.core.ValueObject;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Credential extends ValueObject {

	private final String accessKeyId;
	private final String secretAccessKey;

	public Credential(String accessKeyId, String secretAccessKey) {
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
	}

	public String getAccessKeyId() {
		return accessKeyId;
	}

	public String getSecretAccessKey() {
		return secretAccessKey;
	}

}
