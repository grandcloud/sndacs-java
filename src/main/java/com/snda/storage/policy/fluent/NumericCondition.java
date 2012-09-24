package com.snda.storage.policy.fluent;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface NumericCondition {

	ConditionBuilder equals(Number... value);

	ConditionBuilder notEquals(Number... value);

	ConditionBuilder lessThan(Number... value);

	ConditionBuilder lessThanEquals(Number... value);

	ConditionBuilder greaterThan(Number... value);

	ConditionBuilder greaterThanEquals(Number... value);
}
