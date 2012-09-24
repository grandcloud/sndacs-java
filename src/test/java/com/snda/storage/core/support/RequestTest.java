package com.snda.storage.core.support;
import static org.junit.Assert.assertEquals;

import java.net.URI;

import org.junit.Test;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class RequestTest {

	@Test
	public void testBuildURI() throws Exception {
		Request request = new Request().
				withScheme(Scheme.HTTP).
				withEndpoint("storage-huabei-1.grandcloud.cn").
				withBucket("books").
				withKey("chinese/2012-01-02/当今+时代/语文").
				withSubResource("uploads").
				withParameter("prefix", "前缀");
		URI expected = new URI("http://storage-huabei-1.grandcloud.cn/books/chinese/2012-01-02/%E5%BD%93%E4%BB%8A+%E6%97%B6%E4%BB%A3/%E8%AF%AD%E6%96%87?uploads&prefix=%E5%89%8D%E7%BC%80");
		assertEquals(expected, request.buildURI());
	}
	
	@Test
	public void testBuildURIForService() throws Exception {
		Request request = new Request().
				withScheme(Scheme.HTTP).
				withEndpoint("storage-huabei-1.grandcloud.cn").
				withSubResource("uploads");
		URI expected = new URI("http://storage-huabei-1.grandcloud.cn?uploads");
		assertEquals(expected, request.buildURI());
	}
}
