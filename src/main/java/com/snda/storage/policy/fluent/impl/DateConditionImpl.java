package com.snda.storage.policy.fluent.impl;

import org.joda.time.DateTime;

import com.snda.storage.policy.fluent.DateCondition;
import com.snda.storage.policy.fluent.ConditionBuilder;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class DateConditionImpl extends ConditionImpl<DateTime> implements DateCondition {

	public DateConditionImpl(String conditionKey) {
		super(conditionKey);
	}

	@Override
	public ConditionBuilder equals(DateTime dateTime) {
		return fluentCondition("DateEquals", dateTime);
	}

	@Override
	public ConditionBuilder notEquals(DateTime dateTime) {
		return fluentCondition("DateNotEquals", dateTime);
	}

	@Override
	public ConditionBuilder lessThan(DateTime dateTime) {
		return fluentCondition("DateLessThan", dateTime);
	}

	@Override
	public ConditionBuilder lessThanEquals(DateTime dateTime) {
		return fluentCondition("DateLessThanEquals", dateTime);
	}

	@Override
	public ConditionBuilder greaterThan(DateTime dateTime) {
		return fluentCondition("DateGreaterThan", dateTime);
	}

	@Override
	public ConditionBuilder greaterThanEquals(DateTime dateTime) {
		return fluentCondition("DateGreaterThanEquals", dateTime);
	}

}
