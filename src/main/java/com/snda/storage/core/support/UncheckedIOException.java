package com.snda.storage.core.support;

import java.io.IOException;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class UncheckedIOException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncheckedIOException(IOException cause) {
		super(cause);
	}

	public UncheckedIOException(String message, IOException cause) {
		super(message, cause);
	}


}
