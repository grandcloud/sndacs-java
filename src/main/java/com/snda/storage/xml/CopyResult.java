package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
abstract class CopyResult extends XMLEntity {

	@XmlElement(name = "LastModified")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime lastModified;

	@XmlElement(name = "ETag")
	private String entityTag;

	public CopyResult() {
	}
	
	public CopyResult(String entityTag, DateTime lastModified) {
		this.entityTag = entityTag;
		this.lastModified = lastModified;
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

}
