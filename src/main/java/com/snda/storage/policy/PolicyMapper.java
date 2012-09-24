package com.snda.storage.policy;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface PolicyMapper {

	Policy deserialize(InputStream inputStream);
	
	void serialize(Policy policy, OutputStream outputStream);
}
