package com.snda.storage.integration;
import static com.snda.storage.ByteUnit.TB;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import org.joda.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.LimitInputStream;
import com.snda.storage.Entity;
import com.snda.storage.SNDAStorage;
import com.snda.storage.SNDAStorageBuilder;

/**
 * 
 * @author wangzijian
 * 
 */
public class BigObjectUploadTest {

	private static final Random RANDOM = new Random();
	
	private SNDAStorage storage;
	
	@Test
	public void test() {
		storage.bucket("big.object.test").object(new LocalDateTime().toString()).
			reducedRedundancy().
			entity(new Entity() {
				@Override
				public InputStream getInput() throws IOException {
					return new LimitInputStream(new InputStream() {
						@Override
						public int read() throws IOException {
							return RANDOM.nextInt();
						}
					}, 5 * TB);
				}
	
				@Override
				public long getContentLength() {
					return 5 * TB;
				}
			}).
			upload();
	}
	
	@Before
	public void setUp() {
		storage = new SNDAStorageBuilder().
			connectionTimeout(60 * 1000).
			connectionTimeout(6 * 60 * 1000).
			credential("2O8CN0HJ5BJ41EXSXJDKROQ8G", "Zjc4NjQzMDEtOTM0Zi00NzRmLTg2NDgtMzRlZmY1YzVkOTRl").
			https().
			build();
	}
	
	@After
	public void tearDown() {
		storage.destory();
	}
}
