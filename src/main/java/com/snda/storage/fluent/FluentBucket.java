package com.snda.storage.fluent;

import com.snda.storage.Location;
import com.snda.storage.policy.Policy;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentBucket extends List, Delete {

	FluentObject object(String key);

	Get<Location> location();

	Create location(Location location);

	GetDeletePolicy policy();

	SetPolicy policy(Policy policy);

	boolean exist();
	
	void create();

}
