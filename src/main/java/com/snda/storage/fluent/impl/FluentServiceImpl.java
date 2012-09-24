package com.snda.storage.fluent.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.snda.storage.core.StorageService;
import com.snda.storage.fluent.FluentBucket;
import com.snda.storage.fluent.FluentService;
import com.snda.storage.xml.ListAllMyBucketsResult;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FluentServiceImpl implements FluentService {

	private final StorageService storageService;

	public FluentServiceImpl(StorageService storageService) {
		this.storageService = checkNotNull(storageService);
	}

	@Override
	public ListAllMyBucketsResult listBuckets() {
		return storageService.listBuckets();
	}

	@Override
	public FluentBucket bucket(String name) {
		return new FluentBucketImpl(storageService, name);
	}

}
