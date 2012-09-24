package com.snda.storage.core;

import org.joda.time.DateTime;



/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Condition extends ValueObject {

	private DateTime ifModifiedSince;
	private DateTime ifUnmodifiedSince;
	private String ifMatch;
	private String ifNoneMatch;

	public Condition withIfModifiedSince(DateTime ifModifiedSince) {
		setIfModifiedSince(ifModifiedSince);
		return this;
	}

	public Condition withIfUnmodifiedSince(DateTime ifUnmodifiedSince) {
		setIfUnmodifiedSince(ifUnmodifiedSince);
		return this;
	}

	public Condition withIfMatch(String ifMatch) {
		setIfMatch(ifMatch);
		return this;
	}

	public Condition withIfNoneMatch(String ifNoneMatch) {
		setIfNoneMatch(ifNoneMatch);
		return this;
	}

	public DateTime getIfModifiedSince() {
		return ifModifiedSince;
	}

	public void setIfModifiedSince(DateTime ifModifiedSince) {
		this.ifModifiedSince = ifModifiedSince;
	}

	public DateTime getIfUnmodifiedSince() {
		return ifUnmodifiedSince;
	}

	public void setIfUnmodifiedSince(DateTime ifUnmodifiedSince) {
		this.ifUnmodifiedSince = ifUnmodifiedSince;
	}

	public String getIfMatch() {
		return ifMatch;
	}

	public void setIfMatch(String ifMatch) {
		this.ifMatch = ifMatch;
	}

	public String getIfNoneMatch() {
		return ifNoneMatch;
	}

	public void setIfNoneMatch(String ifNoneMatch) {
		this.ifNoneMatch = ifNoneMatch;
	}

}
