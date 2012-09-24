package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Condition;
import com.snda.storage.policy.fluent.ConditionBuilder;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class ConditionImpl<T> {

	private final String conditionKey;

	public ConditionImpl(String conditionKey) {
		this.conditionKey = conditionKey;
	}

	protected ConditionBuilder fluentCondition(final String conditionType, final T... value) {
		return new ConditionBuilder() {
			@Override
			public Condition build() {
				return Condition.simple(conditionType, conditionKey, value);
			}
		};
	}

}
