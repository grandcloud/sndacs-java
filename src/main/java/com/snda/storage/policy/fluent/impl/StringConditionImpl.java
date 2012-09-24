package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.StringCondition;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class StringConditionImpl extends ConditionImpl<String> implements StringCondition {

	public StringConditionImpl(String conditionKey) {
		super(conditionKey);
	}

	@Override
	public ConditionBuilder equals(String... value) {
		return fluentCondition("StringEquals", value);
	}

	@Override
	public ConditionBuilder notEquals(String... value) {
		return fluentCondition("StringNotEquals", value);
	}

	@Override
	public ConditionBuilder equalsIgnoreCase(String... value) {
		return fluentCondition("StringEqualsIgnoreCase", value);
	}

	@Override
	public ConditionBuilder notEqualsIgnoreCase(String... value) {
		return fluentCondition("StringNotEqualsIgnoreCase", value);
	}

	@Override
	public ConditionBuilder like(String... value) {
		return fluentCondition("StringLike", value);
	}

	@Override
	public ConditionBuilder notLike(String... value) {
		return fluentCondition("StringNotLike", value);
	}

}
