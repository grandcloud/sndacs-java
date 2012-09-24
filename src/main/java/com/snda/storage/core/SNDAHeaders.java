package com.snda.storage.core;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class SNDAHeaders {

	public static final String REQUEST_ID = "x-snda-request-id";
	public static final String DATE = "x-snda-date";
	public static final String SNDA_PREFIX = "x-snda-";
	public static final String META_PREFIX = "x-snda-meta-";
	public static final String STORAGE_CLASS = "x-snda-storage-class";
	public static final String EXPIRATION = "x-snda-expiration";
	public static final String EXPIRATION_DAYS = "x-snda-expiration-days";
	public static final String METADATA_DIRECTIVE = "x-snda-metadata-directive";
	public static final String COPY_SOURCE = "x-snda-copy-source";
	public static final String COPY_SOURCE_RANGE = "x-snda-copy-source-range";
	public static final String COPY_SOURCE_IF_MATCH = "x-snda-copy-source-if-match";
	public static final String COPY_SOURCE_IF_NONE_MATCH = "x-snda-copy-source-if-none-match";
	public static final String COPY_SOURCE_IF_UNMODIFIED_SINCE = "x-snda-copy-source-if-unmodified-since";
	public static final String COPY_SOURCE_IF_MODIFIED_SINCE = "x-snda-copy-source-if-modified-since";

	private SNDAHeaders() {
	}
}
