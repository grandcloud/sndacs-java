package com.snda.storage.fluent;

import java.util.List;

import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.Part;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface CompleteMultipartUpload {

	CompleteMultipartUpload part(Part part);
	
	CompleteMultipartUpload parts(List<Part> parts);
	
	CompleteMultipartUploadResult complete();
}
