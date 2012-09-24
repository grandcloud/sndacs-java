package com.snda.storage.httpclient;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.snda.storage.core.support.Response;
import com.snda.storage.core.support.UncheckedIOException;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpResponseAdapter implements Response {

	private final HttpResponse response;

	public HttpResponseAdapter(HttpResponse response) {
		this.response = checkNotNull(response);
	}

	@Override
	public int getStatus() {
		return response.getStatusLine().getStatusCode();
	}

	@Override
	public Map<String, String> getHeaders() {
		return adaptHeaders(response.getAllHeaders());
	}

	@Override
	public void close() throws IOException {
		EntityUtils.consume(response.getEntity());
	}

	@Override
	public InputStream getInputStream() {
		try {
			return response.getEntity().getContent();
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read response", e);
		}
	}
	
	public static Map<String, String> adaptHeaders(Header[] allHeaders) {
		Map<String, String> headers = Maps.newHashMap();
		for (Header each : allHeaders) {
			headers.put(each.getName(), each.getValue());
		}
		return ImmutableMap.copyOf(headers);
	}
}
