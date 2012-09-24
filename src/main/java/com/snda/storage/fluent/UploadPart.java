package com.snda.storage.fluent;

import java.io.File;
import java.io.InputStream;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.core.UploadPartResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface UploadPart {

	UploadPartResult upload(); 
	
	UploadPart contentMD5(String contentMD5);
	
	UploadPart entity(Entity entity);
	
	UploadPart entity(File file);

	UploadPart entity(long contentLength, InputStream inputStream);

	UploadPart entity(long contentLength, InputSupplier<? extends InputStream> supplier);
}
