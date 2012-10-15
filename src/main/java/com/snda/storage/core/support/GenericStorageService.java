package com.snda.storage.core.support;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.net.HttpHeaders.CACHE_CONTROL;
import static com.google.common.net.HttpHeaders.CONTENT_DISPOSITION;
import static com.google.common.net.HttpHeaders.CONTENT_ENCODING;
import static com.google.common.net.HttpHeaders.CONTENT_LENGTH;
import static com.google.common.net.HttpHeaders.CONTENT_MD5;
import static com.google.common.net.HttpHeaders.CONTENT_RANGE;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.HttpHeaders.ETAG;
import static com.google.common.net.HttpHeaders.EXPIRES;
import static com.google.common.net.HttpHeaders.IF_MATCH;
import static com.google.common.net.HttpHeaders.IF_MODIFIED_SINCE;
import static com.google.common.net.HttpHeaders.IF_NONE_MATCH;
import static com.google.common.net.HttpHeaders.IF_UNMODIFIED_SINCE;
import static com.google.common.net.HttpHeaders.LAST_MODIFIED;
import static com.google.common.net.HttpHeaders.RANGE;
import static com.snda.storage.core.ContentRange.parseContentRange;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE_IF_MATCH;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE_IF_MODIFIED_SINCE;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE_IF_NONE_MATCH;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE_IF_UNMODIFIED_SINCE;
import static com.snda.storage.core.SNDAHeaders.COPY_SOURCE_RANGE;
import static com.snda.storage.core.SNDAHeaders.EXPIRATION_DAYS;
import static com.snda.storage.core.SNDAHeaders.METADATA_DIRECTIVE;
import static com.snda.storage.core.SNDAHeaders.META_PREFIX;
import static com.snda.storage.core.SNDAHeaders.STORAGE_CLASS;
import static com.snda.storage.core.SNDAParameters.DELIMITER;
import static com.snda.storage.core.SNDAParameters.KEY_MARKER;
import static com.snda.storage.core.SNDAParameters.MARKER;
import static com.snda.storage.core.SNDAParameters.MAX_KEYS;
import static com.snda.storage.core.SNDAParameters.MAX_PARTS;
import static com.snda.storage.core.SNDAParameters.MAX_UPLOADS;
import static com.snda.storage.core.SNDAParameters.PART_NUMBER;
import static com.snda.storage.core.SNDAParameters.PART_NUMBER_MARKER;
import static com.snda.storage.core.SNDAParameters.POLICY;
import static com.snda.storage.core.SNDAParameters.PREFIX;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CACHE_CONTROL;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_DISPOSITION;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_ENCODING;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_LANGUAGE;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_TYPE;
import static com.snda.storage.core.SNDAParameters.RESPONSE_EXPIRES;
import static com.snda.storage.core.SNDAParameters.UPLOADS;
import static com.snda.storage.core.SNDAParameters.UPLOAD_ID;
import static com.snda.storage.core.SNDAParameters.UPLOAD_ID_MARKER;
import static com.snda.storage.core.support.HttpDateTimeFormatter.formatDateTime;
import static com.snda.storage.core.support.HttpDateTimeFormatter.parseDateTime;
import static com.snda.storage.core.support.Method.DELETE;
import static com.snda.storage.core.support.Method.GET;
import static com.snda.storage.core.support.Method.HEAD;
import static com.snda.storage.core.support.Method.POST;
import static com.snda.storage.core.support.Method.PUT;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.Closeables;
import com.google.common.net.HttpHeaders;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.snda.storage.Entity;
import com.snda.storage.Location;
import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.SNDAServiceException;
import com.snda.storage.core.Condition;
import com.snda.storage.core.CopyObjectRequest;
import com.snda.storage.core.CopyPartRequest;
import com.snda.storage.core.Credential;
import com.snda.storage.core.GetObjectRequest;
import com.snda.storage.core.ListBucketCriteria;
import com.snda.storage.core.ListMultipartUploadsCriteria;
import com.snda.storage.core.ListPartsCriteria;
import com.snda.storage.core.ObjectCreation;
import com.snda.storage.core.ResponseOverride;
import com.snda.storage.core.SNDAParameters;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;
import com.snda.storage.core.UploadPartRequest;
import com.snda.storage.core.UploadPartResult;
import com.snda.storage.policy.Policy;
import com.snda.storage.xml.CompleteMultipartUpload;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.CopyObjectResult;
import com.snda.storage.xml.CopyPartResult;
import com.snda.storage.xml.CreateBucketConfiguration;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.ListAllMyBucketsResult;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;
import com.snda.storage.xml.ListPartsResult;
import com.snda.storage.xml.LocationConstraint;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class GenericStorageService implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(GenericStorageService.class);
	
	private final LoadingCache<String, Location> locations = CacheBuilder.newBuilder().
			expireAfterWrite(15, TimeUnit.MINUTES).
			build(new CacheLoader<String, Location>() {
				@Override
				public Location load(String bucket) throws Exception {
					return doGetBucketLocation(bucket);
				}
			});

	private final HttpInvoker invoker;
	
	private Scheme scheme = Scheme.DEFAULT;
	private Credential credential;
	
	public GenericStorageService(HttpInvoker invoker) {
		this.invoker = checkNotNull(invoker);
	}

	@Override
	public ListAllMyBucketsResult listBuckets() {
		return invoke(serviceRequest(Location.PREFERRED).method(GET), ListAllMyBucketsResult.class);
	}

	@Override
	public void createBucket(String bucket) {
		createBucket(bucket, new CreateBucketConfiguration(Location.PREFERRED));
	}

	@Override
	public void createBucket(String bucket, CreateBucketConfiguration createBucketConfiguration) {
		checkNotNull(bucket);
		checkNotNull(createBucketConfiguration);
		if (createBucketConfiguration.getLocationConstraint() == null) {
			createBucketConfiguration.setLocationConstraint(Location.PREFERRED);
		}
		invoke(serviceRequest(createBucketConfiguration.getLocationConstraint()).
				bucket(bucket).
				method(PUT).
				entity(createBucketConfiguration));
		locations.put(bucket, createBucketConfiguration.getLocationConstraint());
	}

	@Override
	public ListBucketResult listObjects(String bucket) {
		return listObjects(bucket, new ListBucketCriteria());
	}
	
	@Override
	public ListBucketResult listObjects(String bucket, ListBucketCriteria criteria) {
		checkNotNull(bucket);
		checkNotNull(criteria);
		return invoke(bucketRequest(bucket).
				parameter(DELIMITER, criteria.getDelimiter()).
				parameter(PREFIX, criteria.getPrefix()).
				parameter(MARKER, criteria.getMarker()).
				parameter(MAX_KEYS, criteria.getMaxKeys()).
				method(GET),
				ListBucketResult.class);
	}
	
	@Override
	public ListMultipartUploadsResult listMultipartUploads(String bucket) {
		return listMultipartUploads(bucket, new ListMultipartUploadsCriteria());
	}

	@Override
	public ListMultipartUploadsResult listMultipartUploads(String bucket, ListMultipartUploadsCriteria criteria) {
		checkNotNull(bucket);
		checkNotNull(criteria);
		return invoke(bucketRequest(bucket).
				subResource(UPLOADS).
				parameter(DELIMITER, criteria.getDelimiter()).
				parameter(PREFIX, criteria.getPrefix()).
				parameter(KEY_MARKER, criteria.getKeyMarker()).
				parameter(UPLOAD_ID_MARKER, criteria.getUploadIdMarker()).
				parameter(MAX_UPLOADS, criteria.getMaxUploads()).
				method(GET),
				ListMultipartUploadsResult.class);
	}

	@Override
	public boolean doesBucketExist(String bucket) {
		try {
			invoke(bucketRequest(bucket).method(HEAD), Void.class);
			return true;
		} catch (SNDAServiceException e) {
			if (e.getStatus() == 404) {
				return false;
			}
			throw e;
		}
	}

	@Override
	public Location getBucketLocation(String bucket) {
		try {
			return locations.getUnchecked(bucket);
		} catch (UncheckedExecutionException e) {
			throw Throwables.propagate(e.getCause());
		}
	}

	private Location doGetBucketLocation(String bucket) {
		checkNotNull(bucket);
		return invoke(serviceRequest(Location.PREFERRED).
				bucket(bucket).
				subResource(SNDAParameters.LOCATION).
				method(GET),
				LocationConstraint.class).
				getValue();
	}

	@Override
	public void deleteBucket(String bucket) {
		doDeleteBucket(bucket);
		locations.invalidate(bucket);
	}

	private void doDeleteBucket(String bucket) {
		checkNotNull(bucket);
		invoke(bucketRequest(bucket).method(DELETE));
	}

	@Override
	public void setBucketPolicy(String bucket, Policy policy) {
		checkNotNull(bucket);
		checkNotNull(policy);
		invoke(bucketRequest(bucket).
				method(PUT).
				subResource(POLICY).
				entity(policy));
	}

	@Override
	public Policy getBucketPolicy(String bucket) {
		checkNotNull(bucket);
		return invoke(bucketRequest(bucket).
				subResource(POLICY).
				method(GET),
				Policy.class);
	}

	@Override
	public void deleteBucketPolicy(String bucket) {
		checkNotNull(bucket);
		invoke(bucketRequest(bucket).subResource(POLICY).method(DELETE));
	}

	@Override
	public void deleteObject(String bucket, String key) {
		checkNotNull(bucket);
		checkNotNull(key);
		invoke(objectRequest(bucket, key).method(DELETE));
	}

	@Override
	public SNDAObject downloadObject(String bucket, String key) {
		return downloadObject(bucket, key, new GetObjectRequest());
	}
	
	@Override
	public SNDAObject downloadObject(String bucket, String key, GetObjectRequest getObjectRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(getObjectRequest);
		Response response = invoke(objectGetRequest(bucket, key, getObjectRequest).method(GET),
				Response.class);
		return new SNDAObject().
				withBucket(bucket).
				withKey(key).
				withObjectMetadata(newObjectMetadata(response.getHeaders())).
				withContent(response.getInputStream());
	}

	@Override
	public SNDAObjectMetadata headObject(String bucket, String key) {
		return headObject(bucket, key, new GetObjectRequest());
	}
	
	@Override
	public SNDAObjectMetadata headObject(String bucket, String key, GetObjectRequest getObjectRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(getObjectRequest);
		Response response = null;
		try {
			response = invoke(objectGetRequest(bucket, key, getObjectRequest).method(HEAD), Response.class);
			return newObjectMetadata(response.getHeaders());
		} finally {
			Closeables.closeQuietly(response);
		}
	}
	
	@Override
	public UploadObjectResult uploadObject(String bucket, String key, File file) {
		return uploadObject(bucket, key, new UploadObjectRequest().withEntity(new FileEntity(file)));
	}

	@Override
	public UploadObjectResult uploadObject(String bucket, String key, UploadObjectRequest putObjectRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(putObjectRequest);
		Entity entity = checkNotNull(putObjectRequest.getEntity());
		Response response = null;
		try {
			response = invoke(objectCreateRequest(bucket, key, putObjectRequest.getObjectCreation()).
					header(CONTENT_MD5, putObjectRequest.getContentMD5()).
					method(PUT).
					entity(entity),
					Response.class);
			return new UploadObjectResult(response.getHeaders().get(ETAG));
		} finally {
			Closeables.closeQuietly(response);
		}
	}

	@Override
	public CopyObjectResult copyObject(String bucket, String key, CopyObjectRequest copyObjectRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(copyObjectRequest);
		Condition copyCondition = copyObjectRequest.getCopyCondition();
		return invoke(objectCreateRequest(bucket, key, copyObjectRequest.getObjectCreation()).
				header(COPY_SOURCE, copyObjectRequest.getCopySource()).
				header(COPY_SOURCE_IF_MATCH, copyCondition.getIfMatch()).
				header(COPY_SOURCE_IF_NONE_MATCH, copyCondition.getIfNoneMatch()).
				header(COPY_SOURCE_IF_MODIFIED_SINCE, copyCondition.getIfModifiedSince()).
				header(COPY_SOURCE_IF_UNMODIFIED_SINCE, copyCondition.getIfUnmodifiedSince()).
				header(METADATA_DIRECTIVE, copyObjectRequest.getMetadataDirective()).
				method(PUT),
				CopyObjectResult.class);
	}

	@Override
	public InitiateMultipartUploadResult initiateMultipartUpload(String bucket, String key, ObjectCreation objectCreation) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(objectCreation);
		return invoke(objectCreateRequest(bucket, key, objectCreation).
				subResource(UPLOADS).
				method(POST),
				InitiateMultipartUploadResult.class);
	}

	@Override
	public ListPartsResult listParts(String bucket, String key, String uploadId) {
		return listParts(bucket, key, uploadId, new ListPartsCriteria());
	}
	
	@Override
	public ListPartsResult listParts(String bucket, String key, String uploadId, ListPartsCriteria criteria) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(uploadId);
		checkNotNull(criteria);
		return invoke(multipartUploadRequest(bucket, key, uploadId).
				parameter(MAX_PARTS, criteria.getMaxParts()).
				parameter(PART_NUMBER_MARKER, criteria.getPartNumberMarker()).
				method(GET),
				ListPartsResult.class);
	}

	@Override
	public UploadPartResult uploadPart(String bucket, String key, String uploadId, int partNumber, UploadPartRequest uploadPartRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(uploadId);
		checkNotNull(uploadPartRequest);
		Entity entity = checkNotNull(uploadPartRequest.getEntity());
		Response response = null;
		try {
			response = invoke(multipartUploadRequest(bucket, key, uploadId).
					parameter(PART_NUMBER, String.valueOf(partNumber)).
					header(CONTENT_MD5, uploadPartRequest.getContentMD5()).
					entity(entity).
					method(PUT),
					Response.class);
			return new UploadPartResult(response.getHeaders().get(ETAG));
		} finally {
			Closeables.closeQuietly(response);
		}
	}

	@Override
	public CopyPartResult copyPart(String bucket, String key, String uploadId, int partNumber, CopyPartRequest copyPartRequest) {
		checkNotNull(bucket);
		checkNotNull(key);
		checkNotNull(uploadId);
		checkNotNull(copyPartRequest);
		Condition copyCondition = copyPartRequest.getCopyCondition();
		return invoke(multipartUploadRequest(bucket, key, uploadId).
				parameter(PART_NUMBER, String.valueOf(partNumber)).
				header(COPY_SOURCE, copyPartRequest.getCopySource()).
				header(COPY_SOURCE_RANGE, copyPartRequest.getCopySourceRange()).
				header(COPY_SOURCE_IF_MATCH, copyCondition.getIfMatch()).
				header(COPY_SOURCE_IF_NONE_MATCH, copyCondition.getIfNoneMatch()).
				header(COPY_SOURCE_IF_MODIFIED_SINCE, formatDateTime(copyCondition.getIfModifiedSince())).
				header(COPY_SOURCE_IF_UNMODIFIED_SINCE, formatDateTime(copyCondition.getIfUnmodifiedSince())).
				method(PUT),
				CopyPartResult.class);
	}

	@Override
	public CompleteMultipartUploadResult completeMultipartUpload(String bucket, String key, String uploadId, List<Part> parts) {
		return invoke(multipartUploadRequest(bucket, key, uploadId).
				method(POST).
				entity(new CompleteMultipartUpload(parts)), 
				CompleteMultipartUploadResult.class);
	}

	@Override
	public void abortMultipartUpload(String bucket, String key, String uploadId) {
		invoke(multipartUploadRequest(bucket, key, uploadId).method(DELETE));
	}
	
	private SNDAObjectMetadata newObjectMetadata(Map<String, String> headers) {
		return new SNDAObjectMetadata().
				withETag(headers.get(ETAG)).
				withExpires(headers.get(EXPIRES)).
				withContentType(headers.get(CONTENT_TYPE)).
				withCacheControl(headers.get(CACHE_CONTROL)).
				withContentEncoding(headers.get(CONTENT_ENCODING)).
				withContentDisposition(headers.get(CONTENT_DISPOSITION)).
				withExpirationDays(parseInteger(headers.get(EXPIRATION_DAYS))).
				withLastModified(parseDateTime(headers.get(LAST_MODIFIED))).
				withContentRange(parseContentRange(headers.get(CONTENT_RANGE))).
				withContentLength(parseContentLength(headers.get(CONTENT_LENGTH))).
				withMetadata(filterKeys(headers, new Predicate<String>() {
					@Override
					public boolean apply(String key) {
						return key.startsWith(META_PREFIX);
					}
				}));
	}
	
	private Request.Builder objectCreateRequest(String bucket, String key, ObjectCreation objectCreation) {
		return objectRequest(bucket, key).
				header(CACHE_CONTROL, objectCreation.getCacheControl()).
				header(CONTENT_DISPOSITION, objectCreation.getContentDisposition()).
				header(CONTENT_ENCODING, objectCreation.getContentEncoding()).
				header(CONTENT_TYPE, objectCreation.getContentType()).
				header(EXPIRES, objectCreation.getExpires()).
				header(STORAGE_CLASS, objectCreation.getStorageClass()).
				header(EXPIRATION_DAYS, objectCreation.getExpirationDays()).
				headers(objectCreation.getMetadata());
	}
	
	private Request.Builder objectGetRequest(String bucket, String key, GetObjectRequest getObjectRequest) {
		ResponseOverride responseOverride = getObjectRequest.getResponseOverride();
		Condition condition = getObjectRequest.getCondition();
		return objectRequest(bucket, key).
				parameter(RESPONSE_CONTENT_TYPE, responseOverride.getContentType()).
				parameter(RESPONSE_CONTENT_LANGUAGE, responseOverride.getContentLanguage()).
				parameter(RESPONSE_EXPIRES, responseOverride.getExpires()).
				parameter(RESPONSE_CACHE_CONTROL, responseOverride.getCacheControl()).
				parameter(RESPONSE_CONTENT_DISPOSITION, responseOverride.getContentDisposition()).
				parameter(RESPONSE_CONTENT_ENCODING, responseOverride.getContentEncoding()).
				header(RANGE, getObjectRequest.getRange()).
				header(IF_MATCH, condition.getIfMatch()).
				header(IF_NONE_MATCH, condition.getIfNoneMatch()).
				header(IF_MODIFIED_SINCE, formatDateTime(condition.getIfModifiedSince())).
				header(IF_UNMODIFIED_SINCE, formatDateTime(condition.getIfUnmodifiedSince()));
	}
	
	private Request.Builder multipartUploadRequest(String bucket, String key, String uploadId) {
		return objectRequest(bucket, key).parameter(UPLOAD_ID, uploadId);
	}

	private Request.Builder objectRequest(String bucket, String key) {
		return bucketRequest(bucket).key(key);
	}

	private Request.Builder bucketRequest(String bucket) {
		Location location = getBucketLocation(bucket);
		return serviceRequest(location).bucket(bucket);
	}

	private Request.Builder serviceRequest(Location location) {
		return Request.builder().
				credential(credential).
				scheme(scheme).
				endpoint(location.getEndpoint()).
				header(HttpHeaders.DATE, HttpDateTimeFormatter.formatDateTime(now()));
	}

	private void invoke(Request.Builder request) {
		invoke(request, Void.class);
	}
	
	private <T> T invoke(Request.Builder builder, Class<T> type) {
		Request request = builder.build();
		LOGGER.info("Invoke {} with return {}", request, type);
		T response = invoker.invoke(request, type);
		LOGGER.info("Return {}", response);
		return response;
	}

	protected DateTime now() {
		return new DateTime();
	}

	private static Integer parseInteger(String text) {
		return text == null ? null : Integer.valueOf(text);
	}
	
	private static long parseContentLength(String text) {
		return Long.valueOf(checkNotNull(text, "Missing Content-Length"));
	}
	
	public void setHttps(boolean https) {
		this.scheme = https ? Scheme.HTTPS : Scheme.HTTP;
	}

	@Override
	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	@Override
	public Credential getCredential() {
		return credential;
	}
}
