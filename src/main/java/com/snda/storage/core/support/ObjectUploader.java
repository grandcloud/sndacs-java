package com.snda.storage.core.support;

import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface ObjectUploader {

	UploadObjectResult putObject(String bucket, String key, UploadObjectRequest putObjectRequest);

	void setMultipartUploadSize(long multipartUploadSize);

	void setPartSize(long uploadPartSize);

}
