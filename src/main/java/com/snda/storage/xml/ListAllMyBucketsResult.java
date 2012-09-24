package com.snda.storage.xml;

import java.util.Iterator;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.collect.Lists;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "ListAllMyBucketsResult")
public class ListAllMyBucketsResult extends XMLEntity implements Iterable<BucketSummary> {

	@XmlElementWrapper(name = "Buckets")
	@XmlElement(name = "Bucket")
	private List<BucketSummary> buckets = Lists.newArrayList();

	public ListAllMyBucketsResult() {
	}

	public ListAllMyBucketsResult(List<BucketSummary> buckets) {
		this.buckets = buckets;
	}

	@Override
	public Iterator<BucketSummary> iterator() {
		return buckets.iterator();
	}

	public List<BucketSummary> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<BucketSummary> buckets) {
		this.buckets = buckets;
	}

}
