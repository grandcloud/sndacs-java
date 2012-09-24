package com.snda.storage.fluent;

import java.util.List;

import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface FluentMultipartUpload extends ListParts {

	PutPart partNumber(int partNumber);

	CompleteMultipartUpload part(Part part);

	CompleteMultipartUpload parts(List<Part> parts);

	void abort();
}
