package com.snda.storage.xml;

import java.util.Collection;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class Collections {

	private Collections() {
	}

	public static boolean isEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotEmpty(Collection<?> collection) {
		return collection != null && !collection.isEmpty();
	}
}
