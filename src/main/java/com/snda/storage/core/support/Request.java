package com.snda.storage.core.support;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
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

	private final Method method;
	private final URI uri;
	private final Object entity;
	private final Credential credential;
	private final Map<String, Object> parameters;
	private final Map<String, Object> headers;
	
	public static Builder builder() {
		return new Builder();
	}
	
	private Request(Builder builder) {
		checkNotNull(builder.method, "method");
		checkNotNull(builder.endpoint, "endpoint");
		this.uri = buildURI(builder);
		this.method = builder.method;
		this.credential = builder.credential;
		this.entity = builder.entity;
		this.parameters = Collections.unmodifiableMap(builder.parameters);
		this.headers =  Collections.unmodifiableMap(builder.headers);
	}
	
	private static URI buildURI(Builder builder) {
		checkNotNull(builder.endpoint);
		URIBuilder uriBuilder = new URIBuilder().
				setScheme(Scheme.nullToDefault(builder.scheme).toString()).
				setHost(builder.endpoint);
		String path = new ObjectPathBuilder().bucket(builder.bucket).key(builder.key).build();
		if (isNotBlank(path)) {
			uriBuilder.setPath("/" + path.toString());
		}
		for (Entry<String, Object> each : builder.parameters.entrySet()) {
			uriBuilder.addParameter(each.getKey(), toString(each.getValue()));
		}
		try {
			return uriBuilder.build();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public Method getMethod() {
		return method;
	}

	public URI getURI() {
		return uri;
	}

	public Object getEntity() {
		return entity;
	}

	public Credential getCredential() {
		return credential;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}
	
	private static String toString(Object value) {
		return value == null ? null : value.toString();
	}
	
	public static class Builder {
		
		private Scheme scheme;
		private String endpoint;
		private Method method;
		private Object entity;
		private String bucket;
		private String key;
		private Credential credential;
		private Map<String, Object> parameters = Maps.newHashMap();
		private Map<String, Object> headers = Maps.newHashMap();
		
		private Builder() {
		}
		
		public Request build() {
			return new Request(this);
		}
		
		public Builder scheme(Scheme scheme) {
			this.scheme = scheme;
			return this;
		}

		public Builder endpoint(String endpoint) {
			this.endpoint = endpoint;
			return this;
		}

		public Builder bucket(String bucket) {
			this.bucket = bucket;
			return this;
		}
		
		public Builder key(String key) {
			this.key = key;
			return this;
		}
		
		public Builder credential(Credential credential) {
			this.credential = credential;
			return this;
		}

		public Builder subResource(String subResource) {
			this.parameters.put(subResource, null);
			return this;
		}
		
		public Builder parameter(String name, Object value) {
			checkNotNull(name);
			if (value != null) {
				this.parameters.put(name, value.toString());
			}
			return this;
		}

		public Builder parameters(Map<String, Object> parameters) {
			checkNotNull(parameters);
			this.parameters.putAll(parameters);
			return this;
		}

		public Builder header(String name, Object value) {
			checkNotNull(name);
			if (value != null) {
				this.headers.put(name, value.toString());
			}
			return this;
		}

		public Builder headers(Map<String, Object> headers) {
			checkNotNull(headers);
			this.headers.putAll(headers);
			return this;
		}

		public Builder method(Method method) {
			this.method = method;
			return this;
		}

		public Builder entity(Object entity) {
			this.entity = entity;
			return this;
		}
		
	}
}
