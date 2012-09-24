package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Part extends XMLEntity {

	@XmlElement(name = "PartNumber")
	private Integer partNumber;

	@XmlElement(name = "ETag")
	private String entityTag;

	public Part() {
	}

	public Part(Integer partNumber, String entityTag) {
		this.partNumber = partNumber;
		this.entityTag = entityTag;
	}

	public Integer getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

	public String getEntityTag() {
		return entityTag;
	}

	public void setEntityTag(String entityTag) {
		this.entityTag = entityTag;
	}

}
