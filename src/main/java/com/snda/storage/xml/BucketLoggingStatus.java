package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "BucketLoggingStatus")
public class BucketLoggingStatus extends XMLEntity {

	@XmlElement(name = "LoggingEnabled")
	private Logging loggingEnabled;

	public BucketLoggingStatus() {
	}

	public BucketLoggingStatus(Logging loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	public Logging getLoggingEnabled() {
		return loggingEnabled;
	}

	public void setLoggingEnabled(Logging loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

}
