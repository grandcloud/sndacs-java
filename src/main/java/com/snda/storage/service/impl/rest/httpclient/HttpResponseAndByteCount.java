package com.snda.storage.service.impl.rest.httpclient;

import org.apache.http.HttpResponse;

/**
 * Simple container object to store an HttpResponse object representing the
 * result of a request connection, and a count of the byte size of the
 * associated CS object.
 * <p>
 * This object is used when CS objects are created to associate the response
 * and the actual size of the object as reported back by CS.
 *
 */

public class HttpResponseAndByteCount {
    private final HttpResponse httpResponse;
    private final long byteCount;

    public HttpResponseAndByteCount(HttpResponse httpResponse, long byteCount) {
        this.httpResponse = httpResponse;
        this.byteCount = byteCount;
    }

    public HttpResponse getHttpResponse() {
        return httpResponse;
    }

    public long getByteCount() {
        return byteCount;
    }
}
