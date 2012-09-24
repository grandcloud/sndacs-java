package com.snda.storage.httpclient;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.snda.storage.httpclient.Buffers.buffer;
import static com.snda.storage.httpclient.HttpResponseAdapter.adaptHeaders;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.Entity;
import com.snda.storage.SNDAServiceException;
import com.snda.storage.core.SNDAHeaders;
import com.snda.storage.core.support.Error;
import com.snda.storage.core.support.HttpInvoker;
import com.snda.storage.core.support.Request;
import com.snda.storage.core.support.Response;
import com.snda.storage.core.support.UncheckedIOException;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpClientInvoker implements HttpInvoker {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientInvoker.class);
	
	public static final String RETURN_TYPE = HttpClientInvoker.class.getName() + ".ReturnType";
	public static final String ENTITY_TYPE = HttpClientInvoker.class.getName() + ".EntityType"; 
	
	private final HttpClient client;
	private final MessageResolver messageResolver;

	public HttpClientInvoker(HttpClient client) {
		this(client, new DefaultMessageResolver());
	}

	public HttpClientInvoker(HttpClient client, MessageResolver messageResolver) {
		this.client = checkNotNull(client);
		this.messageResolver = checkNotNull(messageResolver);
	}

	@Override
	public <T> T invoke(Request request, Class<T> type) {
		checkNotNull(request);
		checkNotNull(type);
		HttpUriRequest httpRequest = createHTTPRequest(request);
		HttpContext context = buildContext(request, type);
		HttpResponse httpResponse = null;
		try {
			httpResponse = execute(httpRequest, context);
			checkError(httpRequest, httpResponse);
			return resolveHTTPResponse(type, httpResponse);
		} finally {
			if (type != Response.class) {
				close(httpResponse);
			}
		}
	}

	private <T> void close(HttpResponse httpResponse) {
		if (httpResponse != null) {
			EntityUtils.consumeQuietly(httpResponse.getEntity());
		}
	}

	private <T> HttpContext buildContext(Request request, Class<T> type) {
		HttpContext context = new BasicHttpContext();
		context.setAttribute(ENTITY_TYPE, type(request.getEntity()));
		context.setAttribute(RETURN_TYPE, type);
		return context;
	}

	private HttpUriRequest createHTTPRequest(Request request) {
		HttpUriRequest httpRequest = HttpRequestCreator.create(request.getMethod(), request.buildURI());
		setHeaders(httpRequest, request.getHeaders());
		setEntity(httpRequest, request.getEntity());
		return httpRequest;
	}

	private HttpResponse execute(HttpUriRequest request, HttpContext context) {
		try {
			return client.execute(URIUtils.extractHost(request.getURI()), request, context);
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to execute " + request, e);
		}
	}

	private void setHeaders(HttpUriRequest request, Map<String, Object> headers) {
		for (Entry<String, Object> each : headers.entrySet()) {
			request.addHeader(each.getKey(), each.getValue().toString());
		}
	}

	private void setEntity(HttpUriRequest request, Object object) {
		if (object == null) {
			return;
		}
		checkState(request instanceof HttpEntityEnclosingRequest, "Cannot add entity to http method %s", request.getMethod());
		((HttpEntityEnclosingRequest) request).setEntity(convertEntity(object));
	}

	private void checkError(HttpUriRequest request, HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		LOGGER.info("Response status {}", status);
		if (!isSuccessful(status)) {
			throw convertException(response);
		}
		if (request.containsHeader(SNDAHeaders.COPY_SOURCE)) {
			buffer(response);
			if (hasError(response)) {
				throw convertException(response);
			}
		}
	}

	private boolean isSuccessful(int status) {
		return status >= 200 && status <= 299;
	}

	private SNDAServiceException convertException(HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		Map<String, String> headers = adaptHeaders(response.getAllHeaders());
		Error error = resolveHTTPResponse(Error.class, response);
		throw new SNDAServiceException(status, headers, error);
	}

	private boolean hasError(HttpResponse response) {
		try {
			resolveHTTPResponse(Error.class, response);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private HttpEntity convertEntity(Object entity) {
		if (entity instanceof Entity) {
			return new RequestEntityAdapter((Entity) entity);
		}
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		messageResolver.write(entity, outputStream);
		return new ByteArrayEntity(outputStream.toByteArray());
	}

	@SuppressWarnings("unchecked")
	private <T> T resolveHTTPResponse(Class<T> type, HttpResponse response) {
		if (Void.TYPE == type) {
			return null;
		}
		if (Response.class == type) {
			return (T) new HttpResponseAdapter(response);
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			return messageResolver.read(type, entity.getContent());
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to read response", e);
		} 
	}
	
	private Object type(Object entity) {
		return entity == null ? null : entity.getClass();
	}

}
