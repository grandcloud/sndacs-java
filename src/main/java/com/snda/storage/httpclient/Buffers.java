package com.snda.storage.httpclient;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.BufferedHttpEntity;

import com.snda.storage.core.support.UncheckedIOException;

/*
 * 
 * 
 */
public class Buffers {

	private Buffers() {
	}
	
	public static void buffer(HttpEntityEnclosingRequest request) {
		HttpEntity entity = request.getEntity();
		if (entity != null) {
			request.setEntity(buffered(entity));
		}
	}

	public static void buffer(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			response.setEntity(buffered(entity));
		}
	}

	private static HttpEntity buffered(HttpEntity entity) {
		try {
			return new BufferedHttpEntity(entity);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
