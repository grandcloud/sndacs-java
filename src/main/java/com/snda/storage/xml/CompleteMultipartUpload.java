package com.snda.storage.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.ImmutableList;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "CompleteMultipartUpload")
public class CompleteMultipartUpload extends XMLEntity {

	@XmlElement(name = "Part")
	private List<Part> parts;

	public CompleteMultipartUpload() {
	}

	public CompleteMultipartUpload(Part... parts) {
		this.parts = ImmutableList.copyOf(parts);
	}
	
	public CompleteMultipartUpload(List<Part> parts) {
		this.parts = parts;
	}
	
	public List<Part> getParts() {
		return parts;
	}

	public void setParts(List<Part> uploadedParts) {
		this.parts = uploadedParts;
	}

}
