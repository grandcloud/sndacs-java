package com.snda.storage.fluent;



/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentObject extends DownloadObject, PutObject, Delete {

	FluentMultipartUpload multipartUpload(String uploadId);
}
