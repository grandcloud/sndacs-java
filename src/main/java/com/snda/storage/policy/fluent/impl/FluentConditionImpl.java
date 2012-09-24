package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.FluentCondition;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentConditionImpl implements FluentCondition {

	private final Statement statement;

	public FluentConditionImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentCondition and(ConditionBuilder conditionBuilder) {
		statement.getConditions().add(conditionBuilder.build());
		return this;
	}

	@Override
	public Statement build() {
		return statement;
	}

}
