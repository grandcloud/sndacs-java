package com.snda.storage.policy.fluent.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.FluentCondition;
import com.snda.storage.policy.fluent.FluentConditionAnd;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentConditionImpl implements FluentCondition, FluentConditionAnd {

	private final Statement statement;

	public FluentConditionImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentConditionAnd where(ConditionBuilder conditionBuilder) {
		statement.getConditions().add(conditionBuilder.build());
		return this;
	}

	@Override
	public FluentConditionAnd and(ConditionBuilder conditionBuilder) {
		statement.getConditions().add(conditionBuilder.build());
		return this;
	}

	@Override
	public Statement identifed(String id) {
		checkNotNull(id);
		return statement.withSid(id);
	}

}
