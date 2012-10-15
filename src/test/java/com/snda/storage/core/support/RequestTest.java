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
		Request request = Request.builder().
				method(Method.DELETE).
				scheme(Scheme.HTTP).
				endpoint("storage-huabei-1.grandcloud.cn").
				bucket("books").
				key("chinese/2012-01-02/当今+时代/语文").
				subResource("uploads").
				parameter("prefix", "前缀").
				build();
		URI expected = new URI("http://storage-huabei-1.grandcloud.cn/books/chinese/2012-01-02/%E5%BD%93%E4%BB%8A+%E6%97%B6%E4%BB%A3/%E8%AF%AD%E6%96%87?uploads&prefix=%E5%89%8D%E7%BC%80");
		assertEquals(expected, request.getURI());
	}
	
	@Test
	public void testBuildURIForService() throws Exception {
		Request request = Request.builder().
				method(Method.GET).
				scheme(Scheme.HTTP).
				endpoint("storage-huabei-1.grandcloud.cn").
				subResource("uploads").
				build();
		URI expected = new URI("http://storage-huabei-1.grandcloud.cn?uploads");
		assertEquals(expected, request.getURI());
	}
}
