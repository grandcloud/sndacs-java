package com.snda.storage.policy;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public enum Effect {

	ALLOW, DENY;

	public static Effect named(String name) {
		return valueOf(name.toUpperCase());
	}

	@Override
	public String toString() {
		String string = super.toString();
		return string.substring(0, 1) + string.substring(1).toLowerCase();
	}

}
