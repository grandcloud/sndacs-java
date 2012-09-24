package com.snda.storage.policy.fluent;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentEffect {

	FluentAction toDo(String... action);

	FluentAction toDoNot(String... action);
}
