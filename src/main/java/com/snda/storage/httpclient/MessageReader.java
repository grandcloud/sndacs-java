package com.snda.storage.httpclient;

import java.io.InputStream;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface MessageReader<T> {

	boolean isReadable(Class<?> type);

	T read(Class<T> type, InputStream inputStream);

}
