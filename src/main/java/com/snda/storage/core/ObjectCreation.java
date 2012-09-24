package com.snda.storage.core;

import java.util.Map;

import com.google.common.collect.Maps;
import com.snda.storage.StorageClass;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ObjectCreation extends ValueObject {

	private String cacheControl;
	private String contentDisposition;
	private String contentEncoding;
	private String contentType;
	private String expires;
	private StorageClass storageClass;
	private Integer expirationDays;
	private Map<String, Object> metadata = Maps.newHashMap();

	public ObjectCreation withStorageClass(StorageClass storageClass) {
		setStorageClass(storageClass);
		return this;
	}

	public ObjectCreation withExpirationDays(Integer expirationDays) {
		setExpirationDays(expirationDays);
		return this;
	}

	public ObjectCreation withCacheControl(String cacheControl) {
		setCacheControl(cacheControl);
		return this;
	}

	public ObjectCreation withContentDisposition(String contentDisposition) {
		setContentDisposition(contentDisposition);
		return this;
	}

	public ObjectCreation withContentEncoding(String contentEncoding) {
		setContentEncoding(contentEncoding);
		return this;
	}

	public ObjectCreation withContentType(String contentType) {
		setContentType(contentType);
		return this;
	}

	public ObjectCreation withExpires(String expires) {
		setExpires(expires);
		return this;
	}

	public ObjectCreation withMetadata(String name, Object value) {
		getMetadata().put(name, value);
		return this;
	}

	public ObjectCreation withMetadata(Map<String, Object> metadata) {
		getMetadata().putAll(metadata);
		return this;
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

	public StorageClass getStorageClass() {
		return storageClass;
	}

	public void setStorageClass(StorageClass storageClass) {
		this.storageClass = storageClass;
	}

	public Integer getExpirationDays() {
		return expirationDays;
	}

	public void setExpirationDays(Integer expirationDays) {
		this.expirationDays = expirationDays;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, Object> metadata) {
		this.metadata = metadata;
	}

}
