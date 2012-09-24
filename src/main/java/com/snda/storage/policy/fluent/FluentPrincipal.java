package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentPrincipal {

	FluentEffect isAllowed();

	FluentEffect isNotAllowed();
}
