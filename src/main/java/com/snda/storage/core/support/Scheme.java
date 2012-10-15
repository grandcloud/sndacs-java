package com.snda.storage.core.support;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public enum Scheme {

	HTTP, HTTPS;

	public static final Scheme DEFAULT = HTTP;

	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static Scheme nullToDefault(Scheme scheme) {
		return scheme == null ? DEFAULT : scheme;
	}

}
