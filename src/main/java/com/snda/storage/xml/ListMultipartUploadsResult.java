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
@XmlRootElement(name = "ListMultipartUploadsResult")
public class ListMultipartUploadsResult extends XMLEntity {

	@XmlElement(name = "Bucket")
	private String bucket;

	@XmlElement(name = "Delimiter")
	private String delimiter;

	@XmlElement(name = "Prefix")
	private String prefix;

	@XmlElement(name = "KeyMarker")
	private String keyMarker;

	@XmlElement(name = "UploadIdMarker")
	private String uploadIdMarker;

	@XmlElement(name = "NextKeyMarker")
	private String nextKeyMarker;

	@XmlElement(name = "NextUploadIdMarker")
	private String nextUploadIdMarker;

	@XmlElement(name = "MaxUploads")
	private Integer maxUploads;

	@XmlElement(name = "IsTruncated")
	private Boolean truncated;

	@XmlElement(name = "Upload")
	private List<UploadSummary> uploadSummaries;

	@XmlElement(name = "CommonPrefixes")
	private List<CommonPrefix> commonPrefixes;

	public ListMultipartUploadsResult withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}

	public ListMultipartUploadsResult withDelimiter(String delimiter) {
		setDelimiter(delimiter);
		return this;
	}

	public ListMultipartUploadsResult withPrefix(String prefix) {
		setPrefix(prefix);
		return this;
	}

	public ListMultipartUploadsResult withKeyMarker(String keyMarker) {
		setKeyMarker(keyMarker);
		return this;
	}

	public ListMultipartUploadsResult withUploadIdMarker(String uploadIdMarker) {
		setUploadIdMarker(uploadIdMarker);
		return this;
	}

	public ListMultipartUploadsResult withNextKeyMarker(String nextKeyMarker) {
		setNextKeyMarker(nextKeyMarker);
		return this;
	}

	public ListMultipartUploadsResult withNextUploadIdMarker(String nextUploadIdMarker) {
		setNextUploadIdMarker(nextUploadIdMarker);
		return this;
	}

	public ListMultipartUploadsResult withMaxUploads(Integer MaxUploads) {
		setMaxUploads(MaxUploads);
		return this;
	}

	public ListMultipartUploadsResult withTruncated(Boolean truncated) {
		setTruncated(truncated);
		return this;
	}

	public ListMultipartUploadsResult withUploadSummary(UploadSummary uploadSummary) {
		getUploadSummaries().add(uploadSummary);
		return this;
	}

	public ListMultipartUploadsResult withUploadSummaries(List<UploadSummary> uploadSummaries) {
		if (isNotEmpty(uploadSummaries)) {
			getUploadSummaries().addAll(uploadSummaries);
		}
		return this;
	}

	public ListMultipartUploadsResult withCommonPrefix(CommonPrefix commonPrefix) {
		getCommonPrefixes().add(commonPrefix);
		return this;
	}

	public ListMultipartUploadsResult withCommonPrefixes(List<CommonPrefix> commonPrefixes) {
		if (isNotEmpty(commonPrefixes)) {
			getCommonPrefixes().addAll(commonPrefixes);
		}
		return this;
	}

	public List<UploadSummary> getUploadSummaries() {
		if (uploadSummaries == null) {
			uploadSummaries = Lists.newArrayList();
		}
		return uploadSummaries;
	}

	public List<CommonPrefix> getCommonPrefixes() {
		if (commonPrefixes == null) {
			commonPrefixes = Lists.newArrayList();
		}
		return commonPrefixes;
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
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

	public String getNextKeyMarker() {
		return nextKeyMarker;
	}

	public void setNextKeyMarker(String nextKeyMarker) {
		this.nextKeyMarker = nextKeyMarker;
	}

	public String getNextUploadIdMarker() {
		return nextUploadIdMarker;
	}

	public void setNextUploadIdMarker(String nextUploadIdMarker) {
		this.nextUploadIdMarker = nextUploadIdMarker;
	}

	public Integer getMaxUploads() {
		return maxUploads;
	}

	public void setMaxUploads(Integer maxUploads) {
		this.maxUploads = maxUploads;
	}

	public Boolean isTruncated() {
		return truncated;
	}

	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}

	public void setUploadSummaries(List<UploadSummary> uploadSummaries) {
		this.uploadSummaries = uploadSummaries;
	}

	public void setCommonPrefixes(List<CommonPrefix> commonPrefixes) {
		this.commonPrefixes = commonPrefixes;
	}

}
