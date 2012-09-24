package com.snda.storage.core;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CopySourceTest {

	@Test
	public void testToStringForChinese() {
		String expected = "mybucket/%E4%BD%A0%E5%A5%BD%EF%BC%8C%E4%B8%96%E7%95%8C!";
		String actual = new CopySource("mybucket", "你好，世界!").toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToStringForChineseWithSlash() {
		String expected = "mybucket/chinese/2012-01-02/%E5%BD%93%E4%BB%8A+%E6%97%B6%E4%BB%A3/%E8%AF%AD%E6%96%87";
		String actual = new CopySource("mybucket", "chinese/2012-01-02/当今+时代/语文").toString();
		assertEquals(expected, actual);
	}

	@Test
	public void testToString() {
		String expected = "mybucket/data/key";
		String actual = new CopySource("mybucket", "data/key").toString();
		assertEquals(expected, actual);
	}
}
