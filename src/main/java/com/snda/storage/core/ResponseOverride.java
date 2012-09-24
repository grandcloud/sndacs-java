package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ResponseOverride extends ValueObject {

	private String contentType;
	private String contentLanguage;
	private String expires;
	private String cacheControl;
	private String contentDisposition;
	private String contentEncoding;

	public ResponseOverride withContentType(String contentType) {
		setContentType(contentType);
		return this;
	}

	public ResponseOverride withContentLanguage(String contentLanguage) {
		setContentLanguage(contentLanguage);
		return this;
	}

	public ResponseOverride withExpires(String expires) {
		setExpires(expires);
		return this;
	}

	public ResponseOverride withCacheControl(String cacheControl) {
		setCacheControl(cacheControl);
		return this;
	}

	public ResponseOverride withContentDisposition(String contentDisposition) {
		setContentDisposition(contentDisposition);
		return this;
	}

	public ResponseOverride withContentEncoding(String contentEncoding) {
		setContentEncoding(contentEncoding);
		return this;
	}
	
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentLanguage() {
		return contentLanguage;
	}

	public void setContentLanguage(String contentLanguage) {
		this.contentLanguage = contentLanguage;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String responseExpires) {
		this.expires = responseExpires;
	}

	public String getCacheControl() {
		return cacheControl;
	}

	public void setCacheControl(String cacheControl) {
		this.cacheControl = cacheControl;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public void setContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}

	public void setContentEncoding(String contentEncoding) {
		this.contentEncoding = contentEncoding;
	}

}
