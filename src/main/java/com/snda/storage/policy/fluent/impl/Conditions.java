package com.snda.storage.policy.fluent.impl;

import com.snda.storage.policy.fluent.BooleanCondtion;
import com.snda.storage.policy.fluent.DateCondition;
import com.snda.storage.policy.fluent.IpAddressCondition;
import com.snda.storage.policy.fluent.NumericCondition;
import com.snda.storage.policy.fluent.StringCondition;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Conditions {

	public static DateCondition currentTime() {
		return new DateConditionImpl("snda:CurrentTime");
	}

	public static IpAddressCondition sourceIp() {
		return new IpAddressConditionImpl("snda:SourceIp");
	}

	public static BooleanCondtion secureTransport() {
		return new BooleanCondtionImpl("snda:SecureTransport");
	}

	public static StringCondition userAgent() {
		return new StringConditionImpl("snda:UserAgent");
	}

	public static StringCondition referer() {
		return new StringConditionImpl("snda:Referer");
	}

	public static NumericCondition epochTime() {
		return new NumericConditionImpl("snda:EpochTime");
	}

}
