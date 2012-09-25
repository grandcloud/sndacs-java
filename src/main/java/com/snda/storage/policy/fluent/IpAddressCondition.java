package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface IpAddressCondition {

	ConditionBuilder ipAddress(String... list);

	ConditionBuilder notIpAddress(String... list);
}
