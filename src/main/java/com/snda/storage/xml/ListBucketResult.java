package com.snda.storage.xml;
import static com.snda.storage.xml.Collections.isNotEmpty;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "ListBucketResult")
public class ListBucketResult extends XMLEntity {

	@XmlElement(name = "Name")
	private String bucketName;

	@XmlElement(name = "Prefix")
	private String prefix;

	@XmlElement(name = "Marker")
	private String marker;

	@XmlElement(name = "NextMarker")
	private String nextMarker;

	@XmlElement(name = "MaxKeys")
	private Integer maxKeys;

	@XmlElement(name = "Delimiter")
	private String delimiter;

	@XmlElement(name = "IsTruncated")
	private Boolean truncated;

	@XmlElement(name = "Contents")
	private List<ObjectSummary> objectSummaries;

	@XmlElement(name = "CommonPrefixes")
	private List<CommonPrefix> commonPrefixes;

	public ListBucketResult withBucketName(String bucketName) {
		setBucketName(bucketName);
		return this;
	}

	public ListBucketResult withPrefix(String prefix) {
		setPrefix(prefix);
		return this;
	}

	public ListBucketResult withMarker(String marker) {
		setMarker(marker);
		return this;
	}

	public ListBucketResult withNextMarker(String nextMarker) {
		setNextMarker(nextMarker);
		return this;
	}

	public ListBucketResult withMaxKeys(Integer maxKeys) {
		setMaxKeys(maxKeys);
		return this;
	}

	public ListBucketResult withDelimiter(String delimiter) {
		setDelimiter(delimiter);
		return this;
	}

	public ListBucketResult withTruncated(Boolean truncated) {
		setTruncated(truncated);
		return this;
	}

	public ListBucketResult withObjectSummaries(List<ObjectSummary> objectSummaries) {
		if (isNotEmpty(objectSummaries)) {
			getObjectSummaries().addAll(objectSummaries);
		}
		return this;
	}
	
	public ListBucketResult withObjectSummary(ObjectSummary objectSummary) {
		getObjectSummaries().add(objectSummary);
		return this;
	}

	public ListBucketResult withCommonPrefixes(List<CommonPrefix> commonPrefixes) {
		if (isNotEmpty(commonPrefixes)) {
			getCommonPrefixes().addAll(commonPrefixes);
		}
		return this;
	}

	public ListBucketResult withCommonPrefix(CommonPrefix commonPrefix) {
		getCommonPrefixes().add(commonPrefix);
		return this;
	}
	
	public List<ObjectSummary> getObjectSummaries() {
		if (objectSummaries == null) {
			objectSummaries = Lists.newArrayList();
		}
		return objectSummaries;
	}

	public List<CommonPrefix> getCommonPrefixes() {
		if (commonPrefixes == null) {
			commonPrefixes = Lists.newArrayList();
		}
		return commonPrefixes;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public void setObjectSummaries(List<ObjectSummary> objectSummaries) {
		this.objectSummaries = objectSummaries;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getMarker() {
		return marker;
	}

	public void setMarker(String marker) {
		this.marker = marker;
	}

	public Integer getMaxKeys() {
		return maxKeys;
	}

	public void setMaxKeys(Integer maxKeys) {
		this.maxKeys = maxKeys;
	}

	public Boolean getTruncated() {
		return truncated;
	}

	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getNextMarker() {
		return nextMarker;
	}

	public void setNextMarker(String nextMarker) {
		this.nextMarker = nextMarker;
	}

	public void setCommonPrefixes(List<CommonPrefix> commonPrefixes) {
		this.commonPrefixes = commonPrefixes;
	}

}
