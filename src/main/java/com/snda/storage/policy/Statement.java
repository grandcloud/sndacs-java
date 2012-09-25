package com.snda.storage.policy;

import java.util.List;

import com.google.common.collect.Lists;
import com.snda.storage.core.ValueObject;
import com.snda.storage.policy.fluent.FluentPrincipal;
import com.snda.storage.policy.fluent.impl.FluentPrincipalImpl;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Statement extends ValueObject {

	private String sid;
	private Effect effect;
	private Action action;
	private List<String> resources = Lists.newArrayList();
	private List<Condition> conditions = Lists.newArrayList();

	public static FluentPrincipal allow() {
		return new FluentPrincipalImpl(new Statement().withEffect(Effect.ALLOW));
	}

	public static FluentPrincipal deny() {
		return new FluentPrincipalImpl(new Statement().withEffect(Effect.DENY));
	}
	
	public Statement withSid(String sid) {
		setSid(sid);
		return this;
	}

	public Statement withEffect(Effect effect) {
		setEffect(effect);
		return this;
	}

	public Statement withAction(Action action) {
		setAction(action);
		return this;
	}

	public Statement withAction(String... action) {
		setAction(Action.action(action));
		return this;
	}

	public Statement withNotAction(String... action) {
		setAction(Action.notAction(action));
		return this;
	}

	public Statement withResource(String resource) {
		getResources().add(resource);
		return this;
	}

	public Statement withResources(List<String> resources) {
		getResources().addAll(resources);
		return this;
	}

	public Statement withCondition(String conditionType, String conditionKey, Object... value) {
		getConditions().add(Condition.simple(conditionType, conditionKey, value));
		return this;
	}

	public Statement withCondition(Condition condition) {
		getConditions().add(condition);
		return this;
	}

	public Statement withConditions(List<Condition> conditions) {
		getConditions().addAll(conditions);
		return this;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Effect getEffect() {
		return effect;
	}

	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public List<String> getResources() {
		return resources;
	}

	public void setResources(List<String> resources) {
		this.resources = resources;
	}

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

}
