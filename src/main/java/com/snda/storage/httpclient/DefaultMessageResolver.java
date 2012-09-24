package com.snda.storage.httpclient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class DefaultMessageResolver implements MessageResolver {

	private final List<MessageWriter<?>> writers = Lists.newArrayList();
	private final List<MessageReader<?>> readers = Lists.newArrayList();

	public DefaultMessageResolver() {
		XMLResolver xmlResolver = new XMLResolver();
		writers.add(xmlResolver);
		readers.add(xmlResolver);
		
		PolicyResolver policyResolver = new PolicyResolver();
		writers.add(policyResolver);
		readers.add(policyResolver);
		
		readers.add(new ErrorReader());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T> T read(Class<T> type, InputStream inputStream) {
		for (MessageReader<?> each : readers) {
			if (each.isReadable(type)) {
				return read((MessageReader<T>) each, type, inputStream);
			}
		}
		throw new IllegalStateException("Unrecognized entity type:" + type);
	}

	@Override
	public void write(Object entity, OutputStream outputStream) {
		for (MessageWriter<?> writer : writers) {
			if (writer.isWritable(entity.getClass())) {
				write(writer, entity, outputStream);
				return;
			}
		}
		throw new IllegalStateException("Unrecognized entity type:" + entity.getClass());
	}

	private static <T> T read(MessageReader<T> resolver, Class<T> type, InputStream inputStream) {
		return resolver.read(type, inputStream);
	}

	@SuppressWarnings("unchecked")
	private static <T> void write(MessageWriter<T> writer, Object object, OutputStream outputStream) {
		writer.write((T) object, outputStream);
	}
}
