package com.snda.storage.httpclient;
import static com.google.common.base.Preconditions.*;
import java.net.URI;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;

import com.snda.storage.core.support.Method;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpRequestCreator {

	private HttpRequestCreator() {
	}

	public static HttpUriRequest create(Method method, URI uri) {
		checkNotNull(method);
		checkNotNull(uri);
		switch (method) {
		case GET: {
			return new HttpGet(uri);
		}
		case POST: {
			return new HttpPost(uri);
		}
		case PUT: {
			return new HttpPut(uri);
		}
		case DELETE: {
			return new HttpDelete(uri);
		}
		case HEAD: {
			return new HttpHead(uri);
		}
		default:
			throw new IllegalArgumentException("Illegal Method: " + method);
		}
	}
}
