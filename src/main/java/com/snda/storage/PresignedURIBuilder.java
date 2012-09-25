package com.snda.storage;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CACHE_CONTROL;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_DISPOSITION;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_ENCODING;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_LANGUAGE;
import static com.snda.storage.core.SNDAParameters.RESPONSE_CONTENT_TYPE;
import static com.snda.storage.core.SNDAParameters.RESPONSE_EXPIRES;

import java.net.URI;

import org.joda.time.DateTime;

import com.snda.storage.authorization.Canonicalization;
import com.snda.storage.authorization.HmacSHA1;
import com.snda.storage.core.Credential;
import com.snda.storage.core.ResponseOverride;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.support.CanonicalizableRequestAdapter;
import com.snda.storage.core.support.Method;
import com.snda.storage.core.support.Request;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class PresignedURIBuilder {

	private static final String SNDA_ACCESS_KEY_ID = "SNDAAccessKeyId";
	private static final String SIGNATURE = "Signature";
	private static final String EXPIRES = "Expires";
	
	private final StorageService storageService;
	
	private Method method = Method.GET;
	private String bucket;
	private String key;
	private DateTime expires;
	private ResponseOverride responseOverride = new ResponseOverride();
	
	public PresignedURIBuilder(StorageService storageService) {
		this.storageService = checkNotNull(storageService);
	}
	
	public URI build() {
		checkRequired(method, "method");
		checkRequired(bucket, "bucket");
		checkRequired(key, "key");
		checkRequired(expires, "expires");
		Credential credential = checkRequired(storageService.getCredential(), "credential");
		Request request = new Request().
			withMethod(method).
			withEndpoint(getLocation(bucket).getPublicEndpoint()).
			withBucket(bucket).
			withKey(key).
			withParameter(RESPONSE_CONTENT_TYPE, responseOverride.getContentType()).
			withParameter(RESPONSE_CONTENT_LANGUAGE, responseOverride.getContentLanguage()).
			withParameter(RESPONSE_EXPIRES, responseOverride.getExpires()).
			withParameter(RESPONSE_CACHE_CONTROL, responseOverride.getCacheControl()).
			withParameter(RESPONSE_CONTENT_DISPOSITION, responseOverride.getContentDisposition()).
			withParameter(RESPONSE_CONTENT_ENCODING, responseOverride.getContentEncoding()).
			withParameter(EXPIRES, expires.getMillis() / 1000);
		String sigature = sign(request, credential);
		return request.
			withParameter(SNDA_ACCESS_KEY_ID, credential.getAccessKeyId()).
			withParameter(SIGNATURE, sigature).
			buildURI();
	}
	
	private static <T> T checkRequired(T object, String name) {
		return checkNotNull(object, "%s is required");
	}

	private Location getLocation(String bucket) {
		return storageService.getBucketLocation(bucket);
	}

	private String sign(Request request, Credential credential) {
		String stringToSign = Canonicalization.canonicalize(new CanonicalizableRequestAdapter(request));
		return HmacSHA1.calculate(credential.getSecretAccessKey(), stringToSign);
	}

	public PresignedURIBuilder method(Method method) {
		this.method = method;
		return this;
	}
	
	public PresignedURIBuilder bucket(String bucket) {
		this.bucket = bucket;
		return this;
	}
	
	public PresignedURIBuilder key(String key) {
		this.key = key;
		return this;
	}
	
	public PresignedURIBuilder expires(DateTime expires) {
		this.expires = expires;
		return this;
	}
	
	public PresignedURIBuilder responseContentType(String responseContentType) {
		responseOverride.setContentType(responseContentType);
		return this;
	}

	public PresignedURIBuilder responseContentLanguage(String responseContentLanguage) {
		responseOverride.setContentLanguage(responseContentLanguage);
		return this;
	}

	public PresignedURIBuilder responseExpires(String responseExpires) {
		responseOverride.setExpires(responseExpires);
		return this;
	}

	public PresignedURIBuilder responseCacheControl(String responseCacheControl) {
		responseOverride.setCacheControl(responseCacheControl);
		return this;
	}

	public PresignedURIBuilder responseContentDisposition(String responseContentDisposition) {
		responseOverride.setContentDisposition(responseContentDisposition);
		return this;
	}

	public PresignedURIBuilder responseContentEncoding(String responseContentEncoding) {
		responseOverride.setContentEncoding(responseContentEncoding);
		return this;
	}
}
