package com.snda.storage.core;



/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ListMultipartUploadsCriteria extends ValueObject {

	private String delimiter;
	private Integer maxUploads;
	private String prefix;
	private String keyMarker;
	private String uploadIdMarker;

	public ListMultipartUploadsCriteria withDelimiter(String delimiter) {
		setDelimiter(delimiter);
		return this;
	}

	public ListMultipartUploadsCriteria withMaxUploads(Integer maxUploads) {
		setMaxUploads(maxUploads);
		return this;
	}

	public ListMultipartUploadsCriteria withPrefix(String prefix) {
		setPrefix(prefix);
		return this;
	}

	public ListMultipartUploadsCriteria withKeyMarker(String keyMarker) {
		setKeyMarker(keyMarker);
		return this;
	}

	public ListMultipartUploadsCriteria withUploadIdMarker(String uploadIdMarker) {
		setUploadIdMarker(uploadIdMarker);
		return this;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Integer getMaxUploads() {
		return maxUploads;
	}

	public void setMaxUploads(Integer maxUploads) {
		this.maxUploads = maxUploads;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getKeyMarker() {
		return keyMarker;
	}

	public void setKeyMarker(String keyMarker) {
		this.keyMarker = keyMarker;
	}

	public String getUploadIdMarker() {
		return uploadIdMarker;
	}

	public void setUploadIdMarker(String uploadIdMarker) {
		this.uploadIdMarker = uploadIdMarker;
	}

}
