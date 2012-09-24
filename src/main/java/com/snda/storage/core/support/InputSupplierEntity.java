package com.snda.storage.core.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.InputSupplier;
import com.snda.storage.Entity;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class InputSupplierEntity extends ValueObject implements Entity {

	private final long contentLength;
	private final InputSupplier<? extends InputStream> inputSupplier;

	public InputSupplierEntity(long contentLength, InputSupplier<? extends InputStream> inputSupplier) {
		this.contentLength = contentLength;
		this.inputSupplier = checkNotNull(inputSupplier);
	}

	@Override
	public InputStream getInput() throws IOException {
		return inputSupplier.getInput();
	}

	@Override
	public long getContentLength() {
		return contentLength;
	}

	public InputSupplier<? extends InputStream> getInputSupplier() {
		return inputSupplier;
	}
}
