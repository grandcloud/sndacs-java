package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "InitiateMultipartUploadResult")
public class InitiateMultipartUploadResult extends XMLEntity {

	@XmlElement(name = "Bucket")
	private String bucket;

	@XmlElement(name = "Key")
	private String key;

	@XmlElement(name = "UploadId")
	private String uploadId;

	public InitiateMultipartUploadResult withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}
	
	public InitiateMultipartUploadResult withKey(String key) {
		setKey(key);
		return this;
	}
	
	public InitiateMultipartUploadResult withUploadId(String uploadId) {
		setUploadId(uploadId);
		return this;
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

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

}
