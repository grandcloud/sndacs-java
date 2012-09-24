package com.snda.storage.httpclient;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.protocol.HttpContext;

import com.google.common.util.concurrent.RateLimiter;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class RateLimitInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {

	private final RateLimiter rateLimiter;

	public RateLimitInterceptor(RateLimiter rateLimiter) {
		this.rateLimiter = checkNotNull(rateLimiter);
	}

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		if (!(request instanceof HttpEntityEnclosingRequest)) {
			return;
		}
		HttpEntityEnclosingRequest httpEntityEnclosingRequest = (HttpEntityEnclosingRequest) request;
		HttpEntity entity = httpEntityEnclosingRequest.getEntity();
		if (entity == null) {
			return;
		}
		httpEntityEnclosingRequest.setEntity(new RateLimitEntity(entity));
	}

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return;
		}
		response.setEntity(new RateLimitEntity(entity));
	}

	private class RateLimitEntity extends HttpEntityWrapper {

		public RateLimitEntity(HttpEntity httpEntity) {
			super(httpEntity);
		}

		@Override
		public InputStream getContent() throws IOException {
			return new FilterInputStream(super.getContent()) {

				@Override
				public int read() throws IOException {
					rateLimiter.acquire();
					return super.read();
				}

				@Override
				public int read(byte[] b, int off, int len) throws IOException {
					rateLimiter.acquire(len);
					return super.read(b, off, len);
				}
			};
		}
	}
}
