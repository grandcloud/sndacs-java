package com.snda.storage.core.support;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.utils.URIBuilder;

import com.google.common.collect.Maps;
import com.snda.storage.core.Credential;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Request extends ValueObject {

	private Scheme scheme = Scheme.HTTP;
	private String endpoint;
	private Method method;
	private Object entity;
	private String bucket;
	private String key;
	private Credential credential;
	private Map<String, Object> parameters = Maps.newLinkedHashMap();
	private Map<String, Object> headers = Maps.newLinkedHashMap();
	
	public URI buildURI() {
		URIBuilder builder = new URIBuilder().
				setScheme(getScheme().toString()).
				setHost(getEndpoint());
		String path = new ObjectPathBuilder().bucket(bucket).key(key).build();
		if (isNotBlank(path)) {
			builder.setPath("/" + path.toString());
		}
		for (Entry<String, Object> each : getParameters().entrySet()) {
			builder.addParameter(each.getKey(), toString(each.getValue()));
		}
		try {
			return builder.build();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toString(Object value) {
		return value == null ? null : value.toString();
	}

	public Request withScheme(Scheme scheme) {
		setScheme(scheme);
		return this;
	}

	public Request withEndpoint(String endpoint) {
		setEndpoint(endpoint);
		return this;
	}

	public Request withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}
	
	public Request withKey(String key) {
		setKey(key);
		return this;
	}

	public Request withSubResource(String subResource) {
		getParameters().put(subResource, null);
		return this;
	}

	public Request withCredential(Credential credential) {
		setCredential(credential);
		return this;
	}
	
	public Request withParameter(String name, Object value) {
		checkNotNull(name);
		if (value != null) {
			getParameters().put(name, value.toString());
		}
		return this;
	}

	public Request withParameters(Map<String, Object> parameters) {
		checkNotNull(parameters);
		getParameters().putAll(parameters);
		return this;
	}

	public Request withHeader(String name, Object value) {
		checkNotNull(name);
		if (value != null) {
			getHeaders().put(name, value.toString());
		}
		return this;
	}

	public Request withHeaders(Map<String, Object> headers) {
		checkNotNull(parameters);
		getHeaders().putAll(headers);
		return this;
	}

	public Request withMethod(Method method) {
		setMethod(method);
		return this;
	}

	public Request withEntity(Object entity) {
		setEntity(entity);
		return this;
	}

	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
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

	public Credential getCredential() {
		return credential;
	}

	public void setCredential(Credential credential) {
		this.credential = credential;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

}
