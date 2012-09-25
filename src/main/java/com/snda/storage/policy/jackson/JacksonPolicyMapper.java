package com.snda.storage.policy.jackson;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.MissingNode;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.node.TextNode;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.snda.storage.policy.Action;
import com.snda.storage.policy.Condition;
import com.snda.storage.policy.ConditionEntry;
import com.snda.storage.policy.Effect;
import com.snda.storage.policy.Policy;
import com.snda.storage.policy.PolicyMapper;
import com.snda.storage.policy.Statement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class JacksonPolicyMapper implements PolicyMapper {

	private static class SingletonHolder {
		private static final JacksonPolicyMapper INSTANCE = new JacksonPolicyMapper();
	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	public static JacksonPolicyMapper getInstance() {
		return SingletonHolder.INSTANCE;
	}

	private JacksonPolicyMapper() {
	}
	
	@Override
	public Policy deserialize(InputStream inputStream) {
		try {
			JsonNode root = objectMapper.readValue(inputStream, JsonNode.class);
			return deserializePolicy(root);
		} catch (IOException e) {
			throw new IllegalStateException("The JSON you provided was not well-formed", e);
		}
	}

	@Override
	public void serialize(Policy policy, OutputStream outputStream) {
		try {
			objectMapper.writeValue(outputStream, serializePolicy(policy));
		} catch (IOException e) {
			throw new IllegalStateException("Serialize policy failed, content -> " + policy, e);
		}
	}

	public Policy deserializePolicy(JsonNode root) throws IOException {
		String id = requiredPath(root, "Id").asText();
		List<Statement> statements = Lists.newArrayList();
		for (JsonNode statementNode : requiredPath(root, "Statement")) {
			statements.add(deserializeStatement(statementNode));
		}
		return new Policy().withId(id).withStatements(statements);
	}

	public ObjectNode serializePolicy(Policy policy) throws IOException {
		ObjectNode root = objectMapper.createObjectNode();
		root.put("Id", policy.getId());
		root.put("Statement", serializeStatements(policy.getStatements()));
		return root;
	}

	private ArrayNode serializeStatements(Collection<Statement> statements) {
		ArrayNode statementArrayNode = objectMapper.createArrayNode();
		for (Statement statement : statements) {
			statementArrayNode.add(serializeStatement(statement));
		}
		return statementArrayNode;
	}

	private ObjectNode serializeStatement(Statement statement) {
		ObjectNode statementNode = objectMapper.createObjectNode();
		statementNode.put("Sid", statement.getSid());
		statementNode.put("Effect", statement.getEffect().toString());
		statementNode.put(statement.getAction().getName(), serializeAction(statement.getAction()));
		statementNode.put("Resource", serializeResource(statement.getResources()));
		if (!statement.getConditions().isEmpty()) {
			statementNode.put("Condition", serializeConditions(statement.getConditions()));
		}
		return statementNode;
	}

	private JsonNode serializeResource(List<String> resource) {
		return serializeStringValues(resource);
	}

	private JsonNode serializeAction(Action action) {
		return serializeStringValues(action.getValues());
	}

	private ObjectNode serializeConditions(List<Condition> conditions) {
		ObjectNode conditionNode = objectMapper.createObjectNode();
		for (Condition condition : conditions) {
			conditionNode.put(condition.getType(), serializeCondition(condition));
		}
		return conditionNode;
	}

	private ObjectNode serializeCondition(Condition condition) {
		ObjectNode entryNode = objectMapper.createObjectNode();
		for (ConditionEntry entry : condition.getEntries()) {
			entryNode.put(entry.getKey(), serializeEntry(condition.getType(), entry));
		}
		return entryNode;
	}

	private JsonNode serializeEntry(final String conditionType, ConditionEntry entry) {
		return serializeValues(entry.getValue(), new Function<Object, JsonNode>() {
			@Override
			public JsonNode apply(Object input) {
				return NodeMapper.serialize(conditionType, input);
			}
		});
	}

	private Statement deserializeStatement(JsonNode statementNode) {
		return new Statement().
				withSid(requiredPath(statementNode, "Sid").asText()).
				withEffect(deserializeEffect(requiredPath(statementNode, "Effect").asText())).
				withAction(deserializeAction(statementNode)).
				withResources(deserializeStringValues(requiredPath(statementNode, "Resource"))).
				withConditions(deserializeConditions(statementNode.path("Condition")));
	}

	private Action deserializeAction(JsonNode statementNode) {
		if (statementNode.has("Action")) {
			return new Action("Action", deserializeStringValues(requiredPath(statementNode, "Action")));
		}
		if (statementNode.has("NotAction")) {
			return new Action("NotAction", deserializeStringValues(requiredPath(statementNode, "NotAction")));
		}
		throw new IllegalStateException("Policy is missing required element: Action");
	}

	private List<Condition> deserializeConditions(JsonNode conditionNode) {
		List<Condition> conditions = Lists.newArrayList();
		for (String conditionType : iterable(conditionNode.getFieldNames())) {
			conditions.add(deserializeCondition(conditionType, conditionNode));
		}
		return conditions;
	}

	private Condition deserializeCondition(String conditionType, JsonNode conditionNode) {
		JsonNode entryNode = requiredPath(conditionNode, conditionType.toString());
		return new Condition().
				withType(conditionType).
				withEntries(deserializeEntries(conditionType, entryNode));
	}

	private List<ConditionEntry> deserializeEntries(final String conditionType, JsonNode entryNode) {
		List<ConditionEntry> entries = Lists.newArrayList();
		for (String conditionKey : iterable(entryNode.getFieldNames())) {
			JsonNode valueNode = requiredPath(entryNode, conditionKey);
			entries.add(deserializeEntry(conditionType, conditionKey, valueNode));
		}
		return ImmutableList.copyOf(entries);
	}

	private ConditionEntry deserializeEntry(final String conditionType, String conditionKey, JsonNode valueNode) {
		return new ConditionEntry().
				withKey(conditionKey).
				withValues(deserializeValues(valueNode, new Function<JsonNode, Object>() {
					@Override
					public Object apply(JsonNode jsonNode) {
						return NodeMapper.deserialize(conditionType, jsonNode);
					}
				}));
	}

	private JsonNode serializeStringValues(List<String> values) {
		return serializeValues(values, new Function<Object, JsonNode>() {
			@Override
			public JsonNode apply(Object input) {
				return new TextNode(input.toString());
			}
		});
	}

	private JsonNode serializeValues(List<?> values, Function<Object, JsonNode> function) {
		if (values.size() == 1) {
			return function.apply(getOnlyElement(values));
		} else {
			ArrayNode arrayNode = objectMapper.createArrayNode();
			for (Object each : values) {
				arrayNode.add(function.apply(each));
			}
			return arrayNode;
		}
	}

	private List<String> deserializeStringValues(JsonNode jsonNode) {
		return deserializeValues(jsonNode, new Function<JsonNode, String>() {
			@Override
			public String apply(JsonNode input) {
				return input.asText();
			}

		});
	}

	private <T> List<T> deserializeValues(JsonNode jsonNode, Function<JsonNode, T> function) {
		List<T> values = Lists.newArrayList();
		if (jsonNode.isArray()) {
			for (JsonNode each : jsonNode) {
				values.add(function.apply(each));
			}
		} else {
			values.add(function.apply(jsonNode));
		}
		return values;
	}

	private Effect deserializeEffect(String effect) {
		return Effect.named(effect);
	}

	private static <T> Iterable<T> iterable(final Iterator<T> iterator) {
		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return iterator;
			};
		};
	}

	private JsonNode requiredPath(JsonNode jsonNode, String fieldName) {
		JsonNode path = jsonNode.path(fieldName);
		checkState(!(path instanceof MissingNode), "Policy is missing required element: %s", fieldName);
		return path;
	}

}
