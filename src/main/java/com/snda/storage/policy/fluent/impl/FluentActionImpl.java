package com.snda.storage.policy.fluent.impl;

import com.google.common.collect.ImmutableList;
import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.FluentAction;
import com.snda.storage.policy.fluent.FluentResource;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentActionImpl implements FluentAction {

	private final Statement statement;

	public FluentActionImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentResource to(String... resources) {
		return new FluentResourceImpl(statement.withResources(ImmutableList.copyOf(resources)));
	}
}
