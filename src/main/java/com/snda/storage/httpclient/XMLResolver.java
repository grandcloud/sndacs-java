package com.snda.storage.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class XMLResolver implements MessageWriter<Object>, MessageReader<Object> {

	private static final byte[] XML_DECLARATION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>".getBytes();
	
	private final LoadingCache<Class<?>, JAXBContext> contexts = CacheBuilder.newBuilder()
			.weakKeys()
			.build(new CacheLoader<Class<?>, JAXBContext>() {
				@Override
				public JAXBContext load(Class<?> type) throws Exception {
					return createContext(type);
				}
			});
	
	@Override
	public boolean isReadable(Class<?> type) {
		return type.isAnnotationPresent(XmlRootElement.class);
	}

	@Override
	public boolean isWritable(Class<?> type) {
		return type.isAnnotationPresent(XmlRootElement.class);
	}

	@Override
	public void write(Object entity, OutputStream outputStream) {
		Marshaller marshaller = createMarshaller(entity.getClass());
		try {
			writeXmlDelcaration(outputStream);
			marshaller.marshal(entity, outputStream);
		} catch (JAXBException e) {
			throw Throwables.propagate(e);
		}
	}

	private void writeXmlDelcaration(OutputStream outputStream) {
		try {
			outputStream.write(XML_DECLARATION);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Object read(Class<Object> type, InputStream inputStream) {
		Unmarshaller unmarshaller = createUnmarshaller(type);
		try {
			return unmarshaller.unmarshal(inputStream);
		} catch (JAXBException e) {
			throw new IllegalStateException(e);
		}
	}

	private JAXBContext createContext(Class<?> type) {
		try {
			return JAXBContext.newInstance(type);
		} catch (JAXBException e) {
			throw Throwables.propagate(e);
		}
	}
	
	private Marshaller createMarshaller(Class<?> type) {
		try {
			Marshaller marshaller = contexts.get(type).createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
			return marshaller;
		} catch (JAXBException e) {
			throw Throwables.propagate(e);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e.getCause());
		}
	}
	
	private Unmarshaller createUnmarshaller(Class<?> type) {
		try {
			return contexts.get(type).createUnmarshaller();
		} catch (JAXBException e) {
			throw Throwables.propagate(e);
		} catch (ExecutionException e) {
			throw Throwables.propagate(e.getCause());
		}
	}
}
