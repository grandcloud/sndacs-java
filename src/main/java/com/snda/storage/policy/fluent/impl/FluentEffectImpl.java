package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.FluentAction;
import com.snda.storage.policy.fluent.FluentEffect;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentEffectImpl implements FluentEffect {

	private final Statement statement;

	public FluentEffectImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentAction toDo(String... actions) {
		return new FluentActionImpl(statement.withAction(actions));
	}

	@Override
	public FluentAction toDoNot(String... actions) {
		return new FluentActionImpl(statement.withNotAction(actions));
	}

}
