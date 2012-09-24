package com.snda.storage.core.support;

import java.io.Closeable;
import java.io.InputStream;
import java.util.Map;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface Response extends Closeable {

	int getStatus();
	
	Map<String, String> getHeaders();
	
	InputStream getInputStream();
}
