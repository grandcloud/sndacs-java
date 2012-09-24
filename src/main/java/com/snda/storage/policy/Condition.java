package com.snda.storage.policy;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Condition extends ValueObject {

	private String type;
	private List<ConditionEntry> entries = Lists.newArrayList();

	public static Condition simple(String conditionType, String conditionKey, Object... value) { 
		return new Condition().
				withType(conditionType).
				withEntry(new ConditionEntry().
				withKey(conditionKey).
				withValues(ImmutableList.copyOf(value)));
	}
	
	public Condition withType(String type) {
		setType(type);
		return this;
	}

	public Condition withEntry(ConditionEntry conditionEntry) {
		getEntries().add(conditionEntry);
		return this;
	}

	public Condition withEntries(List<ConditionEntry> conditionEntries) {
		getEntries().addAll(conditionEntries);
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ConditionEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<ConditionEntry> entries) {
		this.entries = entries;
	}

}
