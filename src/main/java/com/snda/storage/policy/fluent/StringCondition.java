package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface StringCondition {

	ConditionBuilder equals(String... value);

	ConditionBuilder notEquals(String... value);

	ConditionBuilder equalsIgnoreCase(String... value);

	ConditionBuilder notEqualsIgnoreCase(String... value);

	ConditionBuilder like(String... value);

	ConditionBuilder notLike(String... value);
}
