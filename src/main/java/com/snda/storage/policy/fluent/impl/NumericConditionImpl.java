package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.NumericCondition;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class NumericConditionImpl extends ConditionImpl<Number> implements NumericCondition {

	public NumericConditionImpl(String conditionKey) {
		super(conditionKey);
	}

	@Override
	public ConditionBuilder equals(Number... value) {
		return fluentCondition("NumericEquals", value);
	}

	@Override
	public ConditionBuilder notEquals(Number... value) {
		return fluentCondition("NumericNotEquals", value);
	}

	@Override
	public ConditionBuilder lessThan(Number... value) {
		return fluentCondition("NumericLessThan", value);
	}

	@Override
	public ConditionBuilder lessThanEquals(Number... value) {
		return fluentCondition("NumericLessThanEquals", value);
	}

	@Override
	public ConditionBuilder greaterThan(Number... value) {
		return fluentCondition("NumericGreaterThan", value);
	}

	@Override
	public ConditionBuilder greaterThanEquals(Number... value) {
		return fluentCondition("NumericGreaterThanEquals", value);
	}

}
