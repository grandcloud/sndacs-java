package com.snda.storage.policy;
import static com.snda.storage.policy.fluent.impl.Conditions.currentTime;
import static com.snda.storage.policy.fluent.impl.Conditions.epochTime;
import static com.snda.storage.policy.fluent.impl.Conditions.referer;
import static com.snda.storage.policy.fluent.impl.Conditions.secureTransport;
import static com.snda.storage.policy.fluent.impl.Conditions.sourceIp;
import static com.snda.storage.policy.fluent.impl.Conditions.userAgent;
import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class PolicyTest {

	@Test
	public void testFluentAPI() {
		Statement actual = Statement.allow().anyone().perform("GetObject").to("mybucket/user/data").identifed("statement-1");
		Statement expected = new Statement().
				withSid("statement-1").
				withEffect(Effect.ALLOW).
				withAction("GetObject").
				withResource("mybucket/user/data");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFluentAPIWithCondition() {
		Statement actual = Statement.allow().anyone().perform("GetObject", "PutObject").to("mybucket/user/data").
				where(currentTime().greaterThan(new DateTime(2009, 04, 16, 12, 00, 00))).
				and(currentTime().lessThan(new DateTime(2009, 04, 16, 15, 00, 00))).
				and(sourceIp().ipAddress("192.168.176.0/24", "192.168.143.0/24")).
				identifed("123");
		
		Statement expected = new Statement().
				withSid("123").
				withEffect(Effect.ALLOW).
				withAction("GetObject", "PutObject").
				withResource("mybucket/user/data").
				withCondition("DateGreaterThan", "snda:CurrentTime", new DateTime(2009, 04, 16, 12, 00, 00)).
				withCondition("DateLessThan", "snda:CurrentTime", new DateTime(2009, 04, 16, 15, 00, 00)).
				withCondition("IpAddress", "snda:SourceIp", "192.168.176.0/24", "192.168.143.0/24");
		assertEquals(expected, actual);
	}
	
	@Test
	public void testFluentAPIWithMoreConditions() {
		Statement publicGetObject = Statement.deny().anyone().perform("GetObject").to("mybucket/user/data").
				where(userAgent().equals("Android", "IOS")).
				and(secureTransport().bool(true)).
				and(referer().notEquals("*.mycompany.com/*", "*.mycompany2.com/*")).
				and(epochTime().lessThan(123456789)).
				identifed("statement-1");
		
		Policy actual = new Policy().
				withId("1234567890").
				withStatement(publicGetObject);
		
		Policy expected = new Policy().
				withId("1234567890").
				withStatement(new Statement().
					withSid("statement-1").
					withEffect(Effect.DENY).
					withAction("GetObject").
					withResource("mybucket/user/data").
					withCondition("StringEquals", "snda:UserAgent", "Android", "IOS").
					withCondition("Bool", "snda:SecureTransport", true).
					withCondition("StringNotEquals", "snda:Referer", "*.mycompany.com/*", "*.mycompany2.com/*").
					withCondition("NumericLessThan", "snda:EpochTime", 123456789));
		assertEquals(expected, actual);
	}

}
