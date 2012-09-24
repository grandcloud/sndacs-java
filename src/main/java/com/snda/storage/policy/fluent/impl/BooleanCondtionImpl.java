package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.fluent.BooleanCondtion;
import com.snda.storage.policy.fluent.ConditionBuilder;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class BooleanCondtionImpl extends ConditionImpl<Boolean> implements BooleanCondtion {

	public BooleanCondtionImpl(String conditionKey) {
		super(conditionKey);
	}

	@Override
	public ConditionBuilder bool(boolean bool) {
		return fluentCondition("Bool", bool);
	}

}
