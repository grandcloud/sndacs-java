package com.snda.storage;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public enum StorageClass {

	STANDARD, 
	REDUCED_REDUNDANCY;

	public static final StorageClass DEFAULT = STANDARD;

	public static StorageClass defaultToNull(StorageClass storageClass) {
		return storageClass == DEFAULT ? null : storageClass;
	}

	public static StorageClass nullToDefault(StorageClass storageClass) {
		return storageClass == null ? DEFAULT : storageClass;
	}
}
