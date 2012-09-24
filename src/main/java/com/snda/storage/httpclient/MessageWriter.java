package com.snda.storage.httpclient;

import java.io.OutputStream;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface MessageWriter<T> {

	boolean isWritable(Class<?> type);

	void write(T object, OutputStream outputStream);
}
