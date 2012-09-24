package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface IpAddressCondition {

	ConditionBuilder whitelisting(String... list);

	ConditionBuilder blacklisting(String... list);
}
