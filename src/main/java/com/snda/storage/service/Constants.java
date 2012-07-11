package com.snda.storage.service;

/**
 * Constants used by the CSService and its implementation classes.
 * 
 * @author snda
 *
 */
public class Constants {

	public static String CS_DEFAULT_HOSTNAME = "storage.grandcloud.cn";
	
	public static final String CS_SIGNEDURL_HOSTNAME = "storage.sdcloud.cn";
	
	public static final String CS_DEFAULT_LOCATION = "huabei-1";
	
	public static String FILE_PATH_DELIM = "/";
	
	public static final char VIRGULE = (char)0x002F;
	public static final char SPACE = (char)0x0020;
	
	/**
     * The default encoding used for text data: UTF-8
     */
	public static final String DEFAULT_ENCODING = "UTF-8";
	
	/**
     * HMAC/SHA1 Algorithm per RFC 2104, used when generating SNDA signatures.
     */
	public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";
	
	/**
     * Default number of objects to include in each chunk of an object listing.
     */
	public static final long DEFAULT_OBJECT_LIST_CHUNK_SIZE = 1000;
	
	public static final String REST_HEADER_DATE = "Date";
	
	public static final String REST_HEADER_LAST_MODIFIED = "Last-Modified";
	
	public static final String REST_HEADER_ETAG = "ETag";
	
	/**
     * Header prefix for general SNDA headers: x-snda-
     */
	public static final String REST_HEADER_PREFIX = "x-snda-";
	
	/**
     * Header prefix for SNDA metadata headers: x-snda-meta-
     */
	public static final String REST_METADATA_PREFIX = "x-snda-meta-";
	
	/**
     * Header prefix for SNDA's alternative date header: x-snda-date
     */
	public static final String REST_METADATA_ALTERNATE_DATE = "x-snda-date";
	
	public static final String REQUESTER_PAYS_BUCKET_FLAG = "x-snda-request-payer=requester";
}
