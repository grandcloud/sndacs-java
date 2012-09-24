package com.snda.storage.policy;

import java.util.List;

import com.google.common.collect.Lists;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ConditionEntry extends ValueObject {

	private String key;
	private List<Object> value = Lists.newArrayList();

	public ConditionEntry withKey(String key) {
		setKey(key);
		return this;
	}

	public ConditionEntry withValue(Object value) {
		getValue().add(value);
		return this;
	}
	
	public ConditionEntry withValues(List<?> values) {
		getValue().addAll(values);
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<Object> getValue() {
		return value;
	}

	public void setValue(List<Object> value) {
		this.value = value;
	}

}
