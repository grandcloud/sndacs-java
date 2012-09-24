package com.snda.storage.httpclient;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpClientFactory {

	public HttpClient create() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10 * 1000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 60 * 1000);
		params.setParameter(CoreConnectionPNames.SO_KEEPALIVE, true);
		params.setParameter(CoreProtocolPNames.USER_AGENT, "SNDACloudStorageJavaSDK/2.0");
		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(128);
		connectionManager.setDefaultMaxPerRoute(32);
		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, params);
		LogInterceptor logInterceptor = new LogInterceptor();
		httpClient.addRequestInterceptor(logInterceptor);
		httpClient.addResponseInterceptor(logInterceptor);
		return httpClient;
	}
}
