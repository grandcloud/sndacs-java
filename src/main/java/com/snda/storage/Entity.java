package com.snda.storage;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.InputSupplier;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface Entity extends InputSupplier<InputStream> {

	long getContentLength();

	InputStream getInput() throws IOException;
}
