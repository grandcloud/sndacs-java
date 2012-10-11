package com.snda.storage.fluent;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.core.UploadObjectResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface UploadObject {

	UploadObjectResult upload();
	
	UploadObject reducedRedundancy();

	UploadObject standardStorage();
	
	UploadObject expirationDays(int expirationDays);

	UploadObject metadata(String name, Object value);
	
	UploadObject metadata(Map<String, Object> metadata);

	UploadObject contentMD5(String contentMD5);

	UploadObject contentType(String contentType);

	UploadObject cacheControl(String cacheControl);

	UploadObject contentDisposition(String contentDisposition);

	UploadObject contentEncoding(String contentEncoding);

	UploadObject expires(String expires);

	UploadObject multipartUploadSize(long multipartUploadSize);
	
	UploadObject partSize(long partSize);
	
	UploadObject entity(Entity entity);
	
	UploadObject entity(File file);

	UploadObject entity(long contentLength, InputStream inputStream);

	UploadObject entity(long contentLength, InputSupplier<? extends InputStream> supplier);
}
