package com.snda.storage.httpclient;

import java.io.InputStream;
import java.io.OutputStream;

import com.snda.storage.policy.Policy;
import com.snda.storage.policy.jackson.JacksonPolicyMapper;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class PolicyResolver implements MessageWriter<Policy>, MessageReader<Policy> {

	@Override
	public boolean isReadable(Class<?> type) {
		return Policy.class == type;
	}

	@Override
	public boolean isWritable(Class<?> type) {
		return Policy.class == type;
	}

	@Override
	public Policy read(Class<Policy> type, InputStream inputStream) {
		return policyMapper().deserialize(inputStream);
	}

	@Override
	public void write(Policy policy, OutputStream outputStream) {
		policyMapper().serialize(policy, outputStream);
	}

	protected JacksonPolicyMapper policyMapper() {
		return JacksonPolicyMapper.getInstance();
	}
}
