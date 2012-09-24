package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

/*
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class UploadSummary extends XMLEntity {

	@XmlElement(name = "Key")
	private String Key;

	@XmlElement(name = "UploadId")
	private String uploadId;

	@XmlElement(name = "Initiated")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime initiated;

	public UploadSummary withKey(String key) {
		setKey(key);
		return this;
	}
	
	public UploadSummary withUploadId(String uploadId) {
		setUploadId(uploadId);
		return this;
	}
	
	public UploadSummary withInitiated(DateTime initiated) {
		setInitiated(initiated);
		return this;
	}
	
	public String getKey() {
		return Key;
	}

	public void setKey(String key) {
		Key = key;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public DateTime getInitiated() {
		return initiated;
	}

	public void setInitiated(DateTime initiated) {
		this.initiated = initiated;
	}

}
