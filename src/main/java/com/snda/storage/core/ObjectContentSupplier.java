package com.snda.storage.core;

import java.io.InputStream;

import com.google.common.io.InputSupplier;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface ObjectContentSupplier extends InputSupplier<InputStream> {

	long getLength();
}
