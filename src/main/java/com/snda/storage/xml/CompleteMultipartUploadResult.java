package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "CompleteMultipartUploadResult")
public class CompleteMultipartUploadResult extends XMLEntity {

	@XmlElement(name = "Location")
	private String location;

	@XmlElement(name = "Bucket")
	private String bucket;

	@XmlElement(name = "Key")
	private String key;

	@XmlElement(name = "ETag")
	private String entityTag;

	public CompleteMultipartUploadResult withLocation(String location) {
		setLocation(location);
		return this;
	}
	
	public CompleteMultipartUploadResult withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}
	
	public CompleteMultipartUploadResult withKey(String key) {
		setKey(key);
		return this;
	}
	
	public CompleteMultipartUploadResult withEntityTag(String entityTag) {
		setEntityTag(entityTag);
		return this;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getEntityTag() {
		return entityTag;
	}

	public void setEntityTag(String entityTag) {
		this.entityTag = entityTag;
	}

}
