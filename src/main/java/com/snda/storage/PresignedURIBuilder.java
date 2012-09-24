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
<<<<<<< HEAD
import com.snda.storage.core.Credential;
=======
<<<<<<< HEAD
import com.snda.storage.core.Credential;
=======
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
import com.snda.storage.core.ResponseOverride;
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
	
	private Method method = Method.GET;
	private Location location = Location.PREFERRED;
	private Credential credential;
	private String bucket;
	private String key;
	private DateTime expires;
	private ResponseOverride responseOverride = new ResponseOverride();
	
	public URI build() {
		checkNotNull(method, "method");
		checkNotNull(location, "location");
		checkNotNull(credential, "credential");
		checkNotNull(bucket, "bucket");
		checkNotNull(key, "key");
		checkNotNull(expires, "expires");
		Request request = new Request().
			withMethod(method).
			withEndpoint(location.getPublicEndpoint()).
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
	
	private String sign(Request request, Credential credential) {
		String stringToSign = Canonicalization.canonicalize(new CanonicalizableRequestAdapter(request));
		return HmacSHA1.calculate(credential.getSecretAccessKey(), stringToSign);
	}

<<<<<<< HEAD
	public PresignedURIBuilder credential(String accessKeyId, String secretAccessKey) {
		this.credential = new Credential(accessKeyId, secretAccessKey);
=======
<<<<<<< HEAD
	public PresignedURIBuilder credential(String accessKeyId, String secretAccessKey) {
		this.credential = new Credential(accessKeyId, secretAccessKey);
=======
	public PresignedURIBuilder credential(Credential credential) {
		this.credential = credential;
>>>>>>> 8f99bbbb80d00fb854a39f29aba59d5b35718d69
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d
		return this;
	}
	
	public PresignedURIBuilder method(Method method) {
		this.method = method;
		return this;
	}
	
	public PresignedURIBuilder location(Location location) {
		this.location = location;
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
