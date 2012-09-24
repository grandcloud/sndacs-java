package com.snda.storage.authorization;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface CanonicalizableRequest {

	String getMethod();

	String getUndecodedPath();

	Map<String, List<String>> getHeaders();

	Map<String, List<String>> getParameters();
}
