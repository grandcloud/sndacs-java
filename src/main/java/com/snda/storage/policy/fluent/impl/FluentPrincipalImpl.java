package com.snda.storage.policy.fluent.impl;
import static com.google.common.base.Preconditions.checkNotNull;

import com.snda.storage.policy.Statement;
import com.snda.storage.policy.fluent.FluentAction;
import com.snda.storage.policy.fluent.FluentPrincipal;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentPrincipalImpl implements FluentPrincipal {

	private final Statement statement;

	public FluentPrincipalImpl(Statement statement) {
		this.statement = checkNotNull(statement);
	}

	@Override
	public FluentAction anyone() {
		return new FluentActionImpl(statement);
	}


}
