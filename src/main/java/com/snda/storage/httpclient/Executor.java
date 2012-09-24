package com.snda.storage.httpclient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface Executor {

	HttpResponse execute(HttpUriRequest request);
}
