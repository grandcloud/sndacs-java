package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentCondition extends StatementBuilder {

	FluentCondition and(ConditionBuilder fluentCondition);
}
