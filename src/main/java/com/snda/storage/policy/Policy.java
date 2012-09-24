package com.snda.storage.policy;

import static com.google.common.collect.Iterables.tryFind;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.snda.storage.core.ValueObject;
import com.snda.storage.policy.fluent.StatementBuilder;
import com.snda.storage.policy.jackson.JacksonPolicyMapper;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Policy extends ValueObject {

	private String id;
	private List<Statement> statements = Lists.newArrayList();

	public Policy withRandomId() {
		setId(UUID.randomUUID().toString());
		return this;
	}

	public Policy withId(String id) {
		setId(id);
		return this;
	}

	public Policy withStatement(Statement statement) {
		addStatement(statement);
		return this;
	}

	public Policy withStatement(StatementBuilder statementBuilder) {
		addStatement(statementBuilder.build());
		return this;
	}

	public Policy withStatements(List<Statement> statements) {
		for (Statement each : statements) {
			addStatement(each);
		}
		return this;
	}

	public void addStatement(Statement statement) {
		if (statement.getSid() == null) {
			statement.setSid(generateSid());
		}
		getStatements().add(statement);
	}

	private String generateSid() {
		return "statement-" + (getStatements().size() + 1);
	}

	public void removeStatement(String sid) {
		Statement statement = getStatement(sid);
		if (statement != null) {
			getStatements().remove(statement);
		}
	}

	public Statement getStatement(final String sid) {
		return tryFind(getStatements(), new Predicate<Statement>() {
			@Override
			public boolean apply(Statement statement) {
				return Objects.equal(statement.getSid(), sid);
			}
		}).orNull();
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
