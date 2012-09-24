package com.snda.storage.service;

import org.apache.http.client.methods.HttpRequestBase;


@Deprecated
public interface CSRequestAuthorizer {
	public void authorizeHttpRequest(HttpRequestBase httpMethod) throws Exception;
}
