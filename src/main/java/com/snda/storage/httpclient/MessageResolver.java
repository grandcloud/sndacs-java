package com.snda.storage.httpclient;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface MessageResolver {

	<T> T read(Class<T> type, InputStream inputStream);

	void write(Object object, OutputStream outputStream);
}
