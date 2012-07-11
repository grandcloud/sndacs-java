package com.snda.storage.service;

import org.apache.http.client.methods.HttpRequestBase;


public interface CSRequestAuthorizer {
	public void authorizeHttpRequest(HttpRequestBase httpMethod) throws Exception;
}
