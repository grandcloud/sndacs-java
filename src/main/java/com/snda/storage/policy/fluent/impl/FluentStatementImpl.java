package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.FluentPrincipal;
import com.snda.storage.policy.fluent.FluentStatement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentStatementImpl implements FluentStatement {

	private final Statement statement;

	public FluentStatementImpl() {
		this.statement = new Statement();
	}

	@Override
	public FluentPrincipal anyone() {
		return new FluentPrincipalImpl(statement);
	}

}
