package com.snda.storage.core.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.snda.storage.Entity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class InputStreamEntity implements Entity {

	private long contentLength;
	private InputStream inputStream;

	public InputStreamEntity(long contentLength, InputStream inputStream) {
		this.contentLength = contentLength;
		this.inputStream = checkNotNull(inputStream);
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	@Override
	public InputStream getInput(){
		return unclosed(inputStream);
	}

	private InputStream unclosed(InputStream inputStream) {
		return new FilterInputStream(inputStream) {
			@Override
			public void close() throws IOException {
			}
		};
	}
	
	public InputStream getRawInputStream() {
		return inputStream;
	}
}
