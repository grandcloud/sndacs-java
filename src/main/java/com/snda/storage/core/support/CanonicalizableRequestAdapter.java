package com.snda.storage.core.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.snda.storage.authorization.CanonicalizableRequest;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CanonicalizableRequestAdapter implements CanonicalizableRequest {

	private final Request request;

	public CanonicalizableRequestAdapter(Request request) {
		this.request = checkNotNull(request);
	}

	@Override
	public String getMethod() {
		return request.getMethod().toString();
	}

	@Override
	public String getUndecodedPath() {
		return request.buildURI().getRawPath();
	}

	@Override
	public Map<String, List<String>> getHeaders() {
		return adapt(request.getHeaders());
	}

	@Override
	public Map<String, List<String>> getParameters() {
		return adapt(request.getParameters());
	}

	private Map<String, List<String>> adapt(Map<String, Object> map) {
		return Maps.transformValues(map, new Function<Object, List<String>>() {
			@Override
			public List<String> apply(Object value) {
				if (value == null) {
					return ImmutableList.of();
				} else {
					return ImmutableList.of(value.toString());
				}
			}
		});
	}
}
