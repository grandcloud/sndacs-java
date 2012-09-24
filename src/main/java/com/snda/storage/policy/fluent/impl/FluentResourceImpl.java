package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.FluentCondition;
import com.snda.storage.policy.fluent.FluentResource;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentResourceImpl implements FluentResource {

	private final Statement statement;

	public FluentResourceImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentCondition where(ConditionBuilder conditionBuilder) {
		return new FluentConditionImpl(statement.withCondition(conditionBuilder.build()));
	}

	@Override
	public Statement build() {
		return statement;
	}

}
