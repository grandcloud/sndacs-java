package com.snda.storage;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.InputSupplier;

/**
 * Entity代表上传的Object内容，由其内容与长度组成。
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface Entity extends InputSupplier<InputStream> {

	/**
	 * 返回Entity的长度。
	 * 
	 * @return
	 */
	long getContentLength();

	/**
	 * 获得Entity内容代表的InputStream，只有当必要的时候，该方法才会被调用以打开一个新的InputStream。
	 */
	InputStream getInput() throws IOException;
}
