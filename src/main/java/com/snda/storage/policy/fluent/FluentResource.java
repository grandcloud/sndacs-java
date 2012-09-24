package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentResource extends StatementBuilder {

	FluentCondition where(ConditionBuilder fluentCondition);
}
