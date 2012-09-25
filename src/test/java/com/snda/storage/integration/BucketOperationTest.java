package com.snda.storage.integration;
import static com.snda.storage.policy.fluent.impl.Conditions.*;
import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.snda.storage.SNDAStorage;
import com.snda.storage.SNDAStorageBuilder;
import com.snda.storage.policy.Policy;
import com.snda.storage.policy.Statement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class BucketOperationTest {

	private SNDAStorage storage;
	private String bucket;

	@Test
	public void testPolicy() {
		Statement statement = Statement.allow().anyone().perform("storage:GetObject").to("*").
				where(currentTime().lessThan(new DateTime(2012, 10, 1, 10, 0, 0))).
				and(currentTime().greaterThan(new DateTime(2012, 10, 10, 10, 0, 0))).
				identifed("public-get-object");
		
		Policy policy = new Policy().
			withRandomId().
			withStatement(statement);
		
		storage.bucket(bucket).policy(policy).set();
		
		Policy policy2 = storage.bucket(bucket).policy().get();
		assertEquals(policy, policy2);
		
		storage.bucket(bucket).policy().delete();
	}
	
	@Before
	public void setUp() {
		bucket = "beijing";
		storage = new SNDAStorageBuilder().
				credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm").
				build();
	}
	
}
