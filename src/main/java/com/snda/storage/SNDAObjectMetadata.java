package com.snda.storage;

import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.collect.Maps;
import com.snda.storage.core.ContentRange;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class SNDAObjectMetadata extends ValueObject {

	private String eTag;
	private DateTime lastModified;
	private long contentLength;
	private ContentRange contentRange;
	private Integer expirationDays;
	private String cacheControl;
	private String contentDisposition;
	private String contentEncoding;
	private String contentType;
	private String expires;
	private Map<String, String> metadata = Maps.newHashMap();
	
	public SNDAObjectMetadata withETag(String etag) {
		setETag(etag);
		return this;
	}

	public SNDAObjectMetadata withLastModified(DateTime lastModified) {
		setLastModified(lastModified);
		return this;
	}

	public SNDAObjectMetadata withContentLength(long contentLength) {
		setContentLength(contentLength);
		return this;
	}

	public SNDAObjectMetadata withContentRange(ContentRange contentRange) {
		setContentRange(contentRange);
		return this;
	}

	public SNDAObjectMetadata withExpirationDays(Integer expirationDays) {
		setExpirationDays(expirationDays);
		return this;
	}

	public SNDAObjectMetadata withCacheControl(String cacheControl) {
		setCacheControl(cacheControl);
		return this;
	}

	public SNDAObjectMetadata withContentDisposition(String contentDisposition) {
		setContentDisposition(contentDisposition);
		return this;
	}

	public SNDAObjectMetadata withContentEncoding(String contentEncoding) {
		setContentEncoding(contentEncoding);
		return this;
	}

	public SNDAObjectMetadata withContentType(String contentType) {
		setContentType(contentType);
		return this;
	}

	public SNDAObjectMetadata withExpires(String expires) {
		setExpires(expires);
		return this;
	}

	public SNDAObjectMetadata withMetadata(String name, String value) {
		getMetadata().put(name, value);
		return this;
	}

	public SNDAObjectMetadata withMetadata(Map<String, String> metadata) {
		getMetadata().putAll(metadata);
		return this;
	}

	public String getETag() {
		return eTag;
	}

	public void setETag(String eTag) {
		this.eTag = eTag;
	}

	public DateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(DateTime lastModified) {
		this.lastModified = lastModified;
	}

	public long getContentLength() {
		return contentLength;
	}

	public void setContentLength(long contentLength) {
		this.contentLength = contentLength;
	}

	public Integer getExpirationDays() {
		return expirationDays;
	}

	public void setExpirationDays(Integer expirationDays) {
		this.expirationDays = expirationDays;
	}

	public ContentRange getContentRange() {
		return contentRange;
	}

	public void setContentRange(ContentRange contentRange) {
		this.contentRange = contentRange;
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

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpires(String expires) {
		this.expires = expires;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

}
