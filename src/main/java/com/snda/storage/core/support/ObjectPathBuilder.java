package com.snda.storage.core.support;

import java.net.URISyntaxException;

import org.apache.http.client.utils.URIBuilder;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ObjectPathBuilder {

	private String bucket;
	private String key;

	public ObjectPathBuilder bucket(String bucket) {
		this.bucket = bucket;
		return this;
	}

	public ObjectPathBuilder key(String key) {
		this.key = key;
		return this;
	}

	public String build() {
		StringBuilder path = new StringBuilder();
		if (bucket != null) {
			path.append(bucket);
			if (key != null) {
				if (!key.startsWith("/")) {
					path.append("/");
				}
				path.append(key);
			}
		}
		return path.toString();
	}

	public String buildEncoded() {
		try {
			String path = build();
			return new URIBuilder().setPath(path).build().getRawPath();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

}
