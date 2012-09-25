package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.fluent.ConditionBuilder;
import com.snda.storage.policy.fluent.IpAddressCondition;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class IpAddressConditionImpl extends ConditionImpl<String> implements IpAddressCondition {

	public IpAddressConditionImpl(String conditionKey) {
		super(conditionKey);
	}

	@Override
	public ConditionBuilder ipAddress(String... list) {
		return fluentCondition("IpAddress", list);
	}

	@Override
	public ConditionBuilder notIpAddress(String... list) {
		return fluentCondition("NotIpAddress", list);
	}

}
