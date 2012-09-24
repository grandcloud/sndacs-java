package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Effect;
import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.FluentEffect;
import com.snda.storage.policy.fluent.FluentPrincipal;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class FluentPrincipalImpl implements FluentPrincipal {

	private final Statement statement;

	public FluentPrincipalImpl(Statement statement) {
		this.statement = statement;
	}

	@Override
	public FluentEffect isAllowed() {
		return new FluentEffectImpl(statement.withEffect(Effect.ALLOW));
	}

	@Override
	public FluentEffect isNotAllowed() {
		return new FluentEffectImpl(statement.withEffect(Effect.DENY));
	}

}
