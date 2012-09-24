package com.snda.storage;

import java.util.Properties;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import com.google.common.util.concurrent.RateLimiter;
import com.snda.storage.core.Credential;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.support.GenericStorageService;
import com.snda.storage.fluent.FluentBucket;
import com.snda.storage.fluent.impl.FluentBucketImpl;
import com.snda.storage.httpclient.HttpClientInvoker;
import com.snda.storage.httpclient.LogInterceptor;
import com.snda.storage.httpclient.RateLimitInterceptor;
import com.snda.storage.xml.ListAllMyBucketsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class SNDAStorageBuilder {

	private static final int DEFAULT_CONNECTION_TIMEOUT = 10 * 1000;
	private static final int DEFAULT_SO_TIMEOUT = 60 * 1000;
	private static final int DEFAULT_MAX_PER_ROUTE = 32;
	private static final int DEFAULT_MAX_TOTAL = 128;
	
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	private int soTimeout = DEFAULT_SO_TIMEOUT;

	private int maxTotal = DEFAULT_MAX_TOTAL;
	private int defaultMaxPerRoute = DEFAULT_MAX_PER_ROUTE;

	private Credential credential;
	private Boolean https;
	private Long bytesPerSecond;

	public SNDAStorage build() {
		final HttpClient httpClient = buildHttpClient();
		final StorageService storageService = buildStorageService(httpClient);
		return new SNDAStorage() {

			@Override
			public ListAllMyBucketsResult listBuckets() {
				return storageService.listBuckets();
			}

			@Override
			public FluentBucket bucket(String name) {
				return new FluentBucketImpl(storageService, name);
			}

			@Override
			public PresignedURIBuilder presignedURIBuilder() {
				return new PresignedURIBuilder(storageService);
			}

			@Override
			public void destory() {
				httpClient.getConnectionManager().shutdown();
			}
		};
	}

	protected StorageService buildStorageService(HttpClient httpClient) {
		GenericStorageService storageService = new GenericStorageService(new HttpClientInvoker(httpClient));
		if (credential != null) {
			storageService.setCredential(credential);
		}
		if (https != null) {
			storageService.setHttps(https);
		}
		return storageService;
	}

	protected DefaultHttpClient buildHttpClient() {
		HttpParams params = new BasicHttpParams();
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, soTimeout);
		params.setParameter(CoreProtocolPNames.USER_AGENT, buildUserAgent());

		PoolingClientConnectionManager connectionManager = new PoolingClientConnectionManager();
		connectionManager.setMaxTotal(maxTotal);
		connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);

		DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, params);

		LogInterceptor logInterceptor = new LogInterceptor();
		httpClient.addRequestInterceptor(logInterceptor);
		httpClient.addResponseInterceptor(logInterceptor);

		if (bytesPerSecond != null) {
			RateLimitInterceptor rateLimitInterceptor = new RateLimitInterceptor(RateLimiter.create(bytesPerSecond));
			httpClient.addRequestInterceptor(rateLimitInterceptor);
			httpClient.addResponseInterceptor(rateLimitInterceptor);
		}
		return httpClient;
	}

	protected String buildUserAgent() {
		return new StringBuilder().append("SNDAStorageService-JavaSDK/1.5 (").
			append("Java ").append(systemProperty("java.version")).append("; ").
			append("Vendor ").append(systemProperty("java.vendor")).append("; ").
			append(systemProperty("os.name")).append(" ").append(systemProperty("os.version")).append("; ").
			append("HttpClient ").append(httpClientVersion()).append(")").toString();
	}

	public SNDAStorageBuilder connectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public SNDAStorageBuilder soTimeout(int soTimeout) {
		this.soTimeout = soTimeout;
		return this;
	}

	public SNDAStorageBuilder maxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
		return this;
	}

	public SNDAStorageBuilder defaultMaxPerRoute(int defaultMaxPerRoute) {
		this.defaultMaxPerRoute = defaultMaxPerRoute;
		return this;
	}

	public SNDAStorageBuilder bytesPerSecond(long bytesPerSecond) {
		this.bytesPerSecond = bytesPerSecond;
		return this;
	}

	public SNDAStorageBuilder credential(String accessKeyId, String secretAccessKey) {
		this.credential = new Credential(accessKeyId, secretAccessKey);
		return this;
	}

	public SNDAStorageBuilder https() {
		this.https = true;
		return this;
	}

	private static String httpClientVersion() {
		try {
			Properties properties = new Properties();
			properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("org/apache/http/version.properties"));
			return properties.getProperty("info.release");
		} catch (Throwable e) {
			return "Unknown";
		}
	}

	private static String systemProperty(String name) {
		try {
			return System.getProperty(name);
		} catch (Throwable throwable) {
			return null;
		}
	}
}
