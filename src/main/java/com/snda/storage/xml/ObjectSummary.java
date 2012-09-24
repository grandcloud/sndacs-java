package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.snda.storage.StorageClass;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ObjectSummary extends XMLEntity {

	@XmlElement(name = "Key")
	private String key;

	@XmlElement(name = "LastModified")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime lastModified;

	@XmlElement(name = "ETag")
	private String entityTag;
	
	@XmlElement(name = "Size")
	private Long size;

	@XmlElement(name = "StorageClass")
	private StorageClass storageClass; 
	
	public ObjectSummary withKey(String key) {
		setKey(key);
		return this;
	}
	
	public ObjectSummary withLastModified(DateTime lastModified) {
		setLastModified(lastModified);
		return this;
	}
	
	public ObjectSummary withEntityTag(String entityTag) {
		setEntityTag(entityTag);
		return this;
	}
	
	public ObjectSummary withSize(Long size) {
		setSize(size);
		return this;
	}
	
	public ObjectSummary withStorageClass(StorageClass storageClass) {
		setStorageClass(storageClass);
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getEntityTag() {
		return entityTag;
	}

	public void setEntityTag(String entityTag) {
		this.entityTag = entityTag;
	}

	public StorageClass getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(StorageClass storageClass) {
		this.storageClass = storageClass;
	}
	
}
