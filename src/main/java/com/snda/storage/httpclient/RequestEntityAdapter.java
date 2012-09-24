package com.snda.storage.httpclient;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

import com.google.common.io.ByteStreams;
import com.snda.storage.Entity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class RequestEntityAdapter extends AbstractHttpEntity {

	private final Entity entity;

	public RequestEntityAdapter(Entity entity) {
		this.entity = checkNotNull(entity);
	}

	@Override
	public long getContentLength() {
		return entity.getContentLength();
	}

	@Override
	public void writeTo(OutputStream outstream) throws IOException {
		ByteStreams.copy(entity, outstream);
	}

	@Override
	public InputStream getContent() throws IOException, IllegalStateException {
		return null;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

}
