package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ListPartsCriteria extends ValueObject {

	private Integer maxParts;
	private Integer partNumberMarker;

	public ListPartsCriteria withMaxParts(Integer maxParts) {
		setMaxParts(maxParts);
		return this;
	}

	public ListPartsCriteria withPartNumberMarker(Integer partNumberMarker) {
		setPartNumberMarker(partNumberMarker);
		return this;
	}

	public Integer getMaxParts() {
		return maxParts;
	}

	public void setMaxParts(Integer maxParts) {
		this.maxParts = maxParts;
	}

	public Integer getPartNumberMarker() {
		return partNumberMarker;
	}

	public void setPartNumberMarker(Integer partNumberMarker) {
		this.partNumberMarker = partNumberMarker;
	}

}
