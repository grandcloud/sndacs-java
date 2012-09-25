package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian
 * 
 */
public interface FluentCondition extends StatementBuilder {

	FluentConditionAnd where(ConditionBuilder conditionBuilder);
}
