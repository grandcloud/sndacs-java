package com.snda.storage.httpclient;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.snda.storage.httpclient.Buffers.buffer;
import static com.snda.storage.httpclient.HttpClientInvoker.ENTITY_TYPE;
import static com.snda.storage.httpclient.HttpClientInvoker.RETURN_TYPE;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.snda.storage.Entity;
import com.snda.storage.core.support.Response;
import com.snda.storage.core.support.UncheckedIOException;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class LogInterceptor implements HttpRequestInterceptor, HttpResponseInterceptor {

	private static final AtomicLong SEQUENCE = new AtomicLong(1);
	private static final String LINE_BREAK = "\n";

	private final Logger logger;
	private Charset defaultCharset = Charsets.UTF_8;
	private String REQUEST_SEQUENCE = LogInterceptor.class.getName() + ".RequestRequence";

	public LogInterceptor() {
		this.logger = LoggerFactory.getLogger(LogInterceptor.class);
	}

	public LogInterceptor(Logger logger) {
		this.logger = checkNotNull(logger);
	}

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
		if (logger.isDebugEnabled()) {
			long sequence = SEQUENCE.getAndIncrement();
			context.setAttribute(REQUEST_SEQUENCE, sequence);
			logRequest(sequence, (HttpUriRequest) request, context);
		}
	}

	@Override
	public void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
		Long sequence = (Long) context.getAttribute(REQUEST_SEQUENCE);
		if (sequence != null) {
			logResponse(sequence, response, context);
		}
	}

	private void logRequest(long sequence, HttpUriRequest request, HttpContext context) {
		StringBuilder log = new StringBuilder();
		log.append("Request").append(LINE_BREAK);
		log.append(sequence).append(" > ").append(request.getMethod()).append(" ").append(request.getURI()).append(LINE_BREAK);
		for (Header header : request.getAllHeaders()) {
			log.append(sequence).append(" > ").append(header.getName()).append(": ").append(header.getValue()).append(LINE_BREAK);
		}
		if (request instanceof HttpEntityEnclosingRequest) {
			HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
			HttpEntity entity = enclosingRequest.getEntity();
			if (entity != null) {
				log.append(sequence).append(" > ").append(LINE_BREAK);
				if (Entity.class.isAssignableFrom((Class<?>) context.getAttribute(ENTITY_TYPE))) {
					log.append("[").append(entity.getContentLength()).append(" bytes of object data]");
				} else {
					buffer(enclosingRequest);
					log.append(toString(enclosingRequest.getEntity()));
				}
			}
		}
		logger.debug(log.toString());
	}

	private void logResponse(long sequence, HttpResponse response, HttpContext context) {
		if (!logger.isDebugEnabled()) {
			return;
		}
		StringBuilder log = new StringBuilder();
		log.append("Response").append(LINE_BREAK);
		log.append(sequence).append(" < ").append(response.getStatusLine().getStatusCode()).append(LINE_BREAK);
		for (Header header : response.getAllHeaders()) {
			log.append(sequence).append(" < ").append(header.getName()).append(": ").append(header.getValue()).append(LINE_BREAK);
		}
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			log.append(sequence).append(" < ").append(LINE_BREAK);
			if (Response.class.isAssignableFrom((Class<?>) context.getAttribute(RETURN_TYPE))) {
				log.append("[").append(entity.getContentLength()).append(" bytes of object data]");
			} else {
				buffer(response);
				log.append(toString(response.getEntity()));
			}
		}
		logger.debug(log.toString());
	}

	private String toString(HttpEntity httpEntity) {
		try {
			return EntityUtils.toString(httpEntity, defaultCharset);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void setDefaultCharset(Charset defaultCharset) {
		this.defaultCharset = defaultCharset;
	}

}
