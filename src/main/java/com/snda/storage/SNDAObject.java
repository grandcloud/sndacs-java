package com.snda.storage;
import static com.google.common.base.Preconditions.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class SNDAObject extends ValueObject implements Closeable {

	private String bucket;
	private String key;
	private SNDAObjectMetadata objectMetadata;
	private InputStream content;

	@Override
	public void close() throws IOException {
		content.close();
	}
	
	public void to(File file) throws IOException {
		checkNotNull(file);
		createParentIfNecessary(file);
		try {
			ByteStreams.copy(content, Files.newOutputStreamSupplier(file));
		} finally {
			Closeables.closeQuietly(content);
		}
	}

	private static void createParentIfNecessary(File file) {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			parent.mkdirs();
		}
	}

	public SNDAObject withBucket(String bucket) {
		setBucket(bucket);
		return this;
	}

	public SNDAObject withKey(String key) {
		setKey(key);
		return this;
	}

	public SNDAObject withObjectMetadata(SNDAObjectMetadata objectMetadata) {
		setObjectMetadata(objectMetadata);
		return this;
	}

	public SNDAObject withContent(InputStream content) {
		setContent(content);
		return this;
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

	public SNDAObjectMetadata getObjectMetadata() {
		return objectMetadata;
	}

	public void setObjectMetadata(SNDAObjectMetadata objectMetadata) {
		this.objectMetadata = objectMetadata;
	}

	public InputStream getContent() {
		return content;
	}

	public void setContent(InputStream content) {
		this.content = content;
	}

}
