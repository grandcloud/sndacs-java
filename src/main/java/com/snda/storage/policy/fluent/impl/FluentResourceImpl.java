package com.snda.storage.policy.fluent.impl;

import com.google.common.collect.ImmutableList;
import com.snda.storage.policy.Statement;
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
	public FluentCondition to(String... resource) {
		return new FluentConditionImpl(statement.withResources(ImmutableList.copyOf(resource)));
	}

}
