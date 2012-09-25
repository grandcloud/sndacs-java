package com.snda.storage.policy;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.snda.storage.core.ValueObject;
import com.snda.storage.policy.jackson.JacksonPolicyMapper;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Policy extends ValueObject {

	private String id;
	private List<Statement> statements = Lists.newArrayList();

	public Policy withId(String id) {
		setId(id);
		return this;
	}

	public Policy withRandomId() {
		setId(UUID.randomUUID().toString());
		return this;
	}

	public Policy withStatement(Statement statement) {
		getStatements().add(statement);
		return this;
	}

	public Policy withStatements(List<Statement> statements) {
		getStatements().addAll(statements);
		return this;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	public void setStatements(List<Statement> statements) {
		this.statements = statements;
	}

	public String toJSON() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		policyMapper().serialize(this, outputStream);
		return new String(outputStream.toByteArray(), Charsets.UTF_8);
	}

	protected JacksonPolicyMapper policyMapper() {
		return JacksonPolicyMapper.getInstance();
	}

}
