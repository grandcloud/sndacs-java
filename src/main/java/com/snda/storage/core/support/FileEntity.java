package com.snda.storage.core.support;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.snda.storage.Entity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class FileEntity implements Entity {

	private final File file;

	public FileEntity(File file) {
		this.file = checkNotNull(file);
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public InputStream getInput() throws IOException {
		return new FileInputStream(file);
	}

	public File getFile() {
		return file;
	}

}
