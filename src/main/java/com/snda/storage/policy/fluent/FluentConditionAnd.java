package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentConditionAnd extends StatementBuilder {

	FluentConditionAnd and(ConditionBuilder conditionBuilder);
}
