package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Logging extends XMLEntity {

	@XmlElement(name = "TargetBucket")
	private String targetBucket;

	@XmlElement(name = "TargetPrefix")
	private String targetPrefix;
	
	public Logging() {
	}

	public Logging(String targetBucket, String targetPrefix) {
		this.targetBucket = targetBucket;
		this.targetPrefix = targetPrefix;
	}

	public String getTargetBucket() {
		return targetBucket;
	}

	public void setTargetBucket(String targetBucket) {
		this.targetBucket = targetBucket;
	}

	public String getTargetPrefix() {
		return targetPrefix;
	}

	public void setTargetPrefix(String targetPrefix) {
		this.targetPrefix = targetPrefix;
	}

}
