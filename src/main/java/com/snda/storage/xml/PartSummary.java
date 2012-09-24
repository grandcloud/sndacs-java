package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class PartSummary extends XMLEntity {

	@XmlElement(name = "PartNumber")
	private Integer partNumber;

	@XmlElement(name = "LastModified")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime lastModified;

	@XmlElement(name = "ETag")
	private String entityTag;

	@XmlElement(name = "Size")
	private Long size;
	
	public PartSummary withPartNumber(Integer partNumber) {
		setPartNumber(partNumber);
		return this;
	}
	
	public PartSummary withLastModified(DateTime lastModified) {
		setLastModified(lastModified);
		return this;
	}
	
	public PartSummary withEntityTag(String entityTag) {
		setEntityTag(entityTag);
		return this;
	}

	public PartSummary withSize(Long size) {
		setSize(size);
		return this;
	}
	
	public Integer getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(Integer partNumber) {
		this.partNumber = partNumber;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public String getEntityTag() {
		return entityTag;
	}

	public void setEntityTag(String entityTag) {
		this.entityTag = entityTag;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

}
