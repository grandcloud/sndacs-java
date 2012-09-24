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
@XmlRootElement(name = "ListPartsResult")
public class ListPartsResult extends XMLEntity {

	@XmlElement(name = "Bucket")
	private String bucket;

	@XmlElement(name = "Key")
	private String key;

	@XmlElement(name = "UploadId")
	private String uploadId;

	@XmlElement(name = "PartNumberMarker")
	private Integer partNumberMarker;

	@XmlElement(name = "NextPartNumberMarker")
	private Integer nextPartNumberMarker;

	@XmlElement(name = "MaxParts")
	private Integer maxParts;

	@XmlElement(name = "IsTruncated")
	private Boolean truncated;

	@XmlElement(name = "Part")
	private List<PartSummary> partSummaries;

	public ListPartsResult withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}
	
	public ListPartsResult withKey(String key) {
		setKey(key);
		return this;
	}
	
	public ListPartsResult withUploadId(String uploadId) {
		setUploadId(uploadId);
		return this;
	}
	
	public ListPartsResult withPartNumberMarker(Integer partNumberMarker) {
		setPartNumberMarker(partNumberMarker);
		return this;
	}
	
	public ListPartsResult withNextPartNumberMarker(Integer nextPartNumberMarker) {
		setNextPartNumberMarker(nextPartNumberMarker);
		return this;
	}
	
	public ListPartsResult withMaxParts(Integer maxParts) {
		setMaxParts(maxParts);
		return this;
	}
	
	public ListPartsResult withTruncated(Boolean truncated) {
		setTruncated(truncated);
		return this;
	}
	
	public ListPartsResult withPartSummary(PartSummary partSummary) {
		getPartSummaries().add(partSummary);
		return this;
	}
	
	public ListPartsResult withPartSummaries(List<PartSummary> partSummaries) {
		if (isNotEmpty(partSummaries)) {
			getPartSummaries().addAll(partSummaries);
		}
		return this;
	}
	
	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUploadId() {
		return uploadId;
	}

	public void setUploadId(String uploadId) {
		this.uploadId = uploadId;
	}

	public Integer getPartNumberMarker() {
		return partNumberMarker;
	}

	public void setPartNumberMarker(Integer partNumberMarker) {
		this.partNumberMarker = partNumberMarker;
	}

	public Integer getNextPartNumberMarker() {
		return nextPartNumberMarker;
	}

	public void setNextPartNumberMarker(Integer nextPartNumberMarker) {
		this.nextPartNumberMarker = nextPartNumberMarker;
	}

	public Integer getMaxParts() {
		return maxParts;
	}

	public void setMaxParts(Integer maxParts) {
		this.maxParts = maxParts;
	}

	public Boolean getTruncated() {
		return truncated;
	}

	public void setTruncated(Boolean truncated) {
		this.truncated = truncated;
	}

	public List<PartSummary> getPartSummaries() {
		if (partSummaries == null) {
			partSummaries = Lists.newArrayList();
		}
		return partSummaries;
	}

	public void setPartSummaries(List<PartSummary> partSummaries) {
		this.partSummaries = partSummaries;
	}

}
