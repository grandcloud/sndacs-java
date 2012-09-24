package com.snda.storage.core.support;
import java.io.IOException;
import java.io.InputStream;
import static org.mockito.Mockito.*;
import org.junit.Test;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class InputStreamEntityTest {

	@Test
	public void testUnclosedInputStream() throws IOException {
		InputStream inputStream = mock(InputStream.class);
		
		InputStreamEntity entity = new InputStreamEntity(1234L, inputStream);
		entity.getInput().close();
		
		verify(inputStream, never()).close();
	}
}
