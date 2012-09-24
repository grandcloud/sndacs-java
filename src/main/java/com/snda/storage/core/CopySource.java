package com.snda.storage.core;

import static com.google.common.base.Preconditions.checkNotNull;

import com.snda.storage.core.support.ObjectPathBuilder;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CopySource extends ValueObject {

	private final String bucket;
	private final String key;

	public CopySource(String bucket, String key) {
		this.bucket = checkNotNull(bucket);
		this.key = checkNotNull(key);
	}

	@Override
	public String toString() {
		return new ObjectPathBuilder().bucket(bucket).key(key).buildEncoded();
	}

	public String getBucket() {
		return bucket;
	}

	public String getKey() {
		return key;
	}

}
