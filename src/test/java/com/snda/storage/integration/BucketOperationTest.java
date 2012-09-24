package com.snda.storage.integration;
<<<<<<< HEAD
import static com.snda.storage.policy.fluent.impl.Conditions.currentTime;
import static org.junit.Assert.assertEquals;

=======
<<<<<<< HEAD
import static com.snda.storage.policy.fluent.impl.Conditions.currentTime;
import static org.junit.Assert.assertEquals;

=======
import static com.snda.storage.policy.fluent.impl.Conditions.*;
import static org.junit.Assert.*;
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

<<<<<<< HEAD
=======
<<<<<<< HEAD
=======
import com.snda.storage.Credential;
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
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
		Policy policy = new Policy().
			withRandomId().
			withStatement(Statement.that().anyone().isAllowed().toDo("storage:GetObject").to("*").
					where(currentTime().lessThan(new DateTime(2012, 10, 1, 10, 0, 0))).
					and(currentTime().greaterThan(new DateTime(2012, 10, 10, 10, 0, 0))));
		
		storage.bucket(bucket).policy(policy).set();
		
		Policy policy2 = storage.bucket(bucket).policy().get();
		assertEquals(policy, policy2);
		
		storage.bucket(bucket).policy().delete();
	}
	
	@Before
	public void setUp() {
		bucket = "beijing";
		storage = new SNDAStorageBuilder().
<<<<<<< HEAD
				credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm").
=======
<<<<<<< HEAD
				credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm").
=======
				credential(new Credential("BMC5QLEDM156VY5HFNS4T0STT", "MGMzMDEwNTMtZWYwYy00ZGM4LWExNWMtMWZmMjliYTllODZm")).
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
				build();
	}
	
}
