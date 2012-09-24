package com.snda.storage.core.support;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Error extends ValueObject {

	public static final Error EMPTY = new Error(ImmutableMap.<String, String> of());

	private final Map<String, String> map;

	public Error(Map<String, String> map) {
		this.map = ImmutableMap.copyOf(map);
	}

	public String get(String name) {
		return map.get(name);
	}

	public Map<String, String> getMap() {
		return map;
	}

}
