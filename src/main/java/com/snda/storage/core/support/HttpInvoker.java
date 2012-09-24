package com.snda.storage.core.support;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface HttpInvoker {

	<T> T invoke(Request request, Class<T> type);
}
