package com.snda.storage.core;



/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ListBucketCriteria extends ValueObject {

	private String marker;
	private String prefix;
	private String delimiter;
	private Integer maxKeys;

	public ListBucketCriteria withMarker(String marker) {
		setMarker(marker);
		return this;
	}

	public ListBucketCriteria withPrefix(String prefix) {
		setPrefix(prefix);
		return this;
	}

	public ListBucketCriteria withDelimiter(String delimiter) {
		setDelimiter(delimiter);
		return this;
	}

	public ListBucketCriteria withMaxKeys(Integer maxKeys) {
		setMaxKeys(maxKeys);
		return this;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public Integer getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(Integer maxKeys) {
		this.maxKeys = maxKeys;
	}

}
