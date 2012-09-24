package com.snda.storage.policy.jackson;
import static com.snda.storage.policy.Action.GET_OBJECT;
import static com.snda.storage.policy.Action.PUT_OBJECT;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.snda.storage.policy.Condition;
import com.snda.storage.policy.Effect;
import com.snda.storage.policy.Policy;
import com.snda.storage.policy.Statement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class JacksonPolicyMapperTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(JacksonPolicyMapperTest.class);
	
	private JacksonPolicyMapper policyMapper;
	
	@Test
	public void testSimple() throws IOException {
		Policy policy = new Policy().
				withId("aaaa-bbbb-cccc-dddd").
				withStatement(new Statement().
				withSid("1").
				withEffect(Effect.ALLOW).
				withAction("*").
				withResource("*").
				withCondition("StringNotLike", "snda:Referer", "http://*.baidu.com/*").
				withCondition("StringLike", "snda:Referer", "http://www.mysite.com/*", "http://mysite.com/*").
				withCondition("DateLessThan", "snda:CurrentTime", new DateTime("2012-07-21T14:00:00Z")));
		
		assertEquals(policy, deserialize(content("simple.policy")));
		String serialized = serialize(policy);
		LOGGER.info("Serialized {}", serialized);
		assertEquals(policy, deserialize(serialized));
		
	}
	
	
	@Test
	public void testComplex() throws IOException {
		Policy policy = new Policy().withId("aaaa-bbbb-cccc-dddd").
				withStatement(new Statement().
				withSid("1").
				withEffect(Effect.ALLOW).
				withAction(GET_OBJECT, PUT_OBJECT).
				withResource("srn:snda:storage:::mybucket/books/*").
				withResource("srn:snda:storage:::mybucket/mp3/*").
				withCondition("Bool", "snda:SecureTransport", true).
				withCondition("NumericEquals", "snda:EpochTime", 22222).
				withCondition("StringLike", "snda:Referer", "http://www.mysite.com/*", "http://mysite.com/*"));
		assertEquals(policy, deserialize(content("complex.policy")));
		assertEquals(content("complex_in_single_line.policy"), serialize(policy));
	}
	
	@Test
	public void testDeserializeWithMissingCondition() throws IOException {
		Policy expected = new Policy().
				withId("aaaa-bbbb-cccc-dddd").
				withStatement(new Statement().
				withSid("1").
				withEffect(Effect.ALLOW).
				withAction("*").
				withResource("*"));
		assertEquals(expected, deserialize(content("missing_condition.policy")));
		assertEquals(expected, deserialize(content("empty_condition.policy")));
		assertEquals(expected, deserialize(content("simple_without_conditions.policy")));
	}
	
	private Policy deserialize(String s) {
		return policyMapper.deserialize(new ByteArrayInputStream(s.getBytes(Charsets.UTF_8)));
	}
	
	private String serialize(Policy policy) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		policyMapper.serialize(policy, outputStream);
		return new String(outputStream.toByteArray(), Charsets.UTF_8);
	}
	
	@Before
	public void setUp() {
		policyMapper = JacksonPolicyMapper.getInstance();
	}
	
	private static String content(String file) throws IOException {
		return Resources.toString(Condition.class.getResource(file), Charsets.UTF_8);
	}
}
