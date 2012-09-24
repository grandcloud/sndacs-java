package com.snda.storage.policy.fluent;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface DateCondition {

	ConditionBuilder equals(DateTime dateTime);

	ConditionBuilder notEquals(DateTime dateTime);

	ConditionBuilder lessThan(DateTime dateTime);

	ConditionBuilder lessThanEquals(DateTime dateTime);

	ConditionBuilder greaterThan(DateTime dateTime);

	ConditionBuilder greaterThanEquals(DateTime dateTime);
}
