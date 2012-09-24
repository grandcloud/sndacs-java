package com.snda.storage.policy.jackson;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.BooleanNode;
import org.codehaus.jackson.node.DecimalNode;
import org.codehaus.jackson.node.NullNode;
import org.codehaus.jackson.node.TextNode;
import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
class NodeMapper {

	private NodeMapper() {
	}

	public static JsonNode serialize(String conditionType, Object object) {
		checkNotNull(conditionType);
		if (object == null) {
			return NullNode.instance;
		}
		if (isBoolean(conditionType)) {
			return (Boolean) object ? BooleanNode.TRUE : BooleanNode.FALSE;
		}
		if (isNumeric(conditionType)) {
			return new DecimalNode(new BigDecimal(object.toString()));
		}
		return new TextNode(object.toString());
	}

	public static Object deserialize(String conditionType, JsonNode jsonNode) {
		checkNotNull(conditionType);
		checkNotNull(jsonNode);
		if (jsonNode instanceof NullNode) {
			return null;
		}
		if (isBoolean(conditionType)) {
			return jsonNode.getBooleanValue();
		}
		if (isNumeric(conditionType)) {
			return jsonNode.getNumberValue();
		}
		if (isDateTime(conditionType)) {
			return new DateTime(jsonNode.asText());
		}
		return jsonNode.asText();
	}

	private static boolean isBoolean(String conditionType) {
		return conditionType.equals("Bool");
	}

	private static boolean isDateTime(String conditionType) {
		return conditionType.startsWith("Date");
	}

	private static boolean isNumeric(String conditionType) {
		return conditionType.startsWith("Numeric");
	}
}
