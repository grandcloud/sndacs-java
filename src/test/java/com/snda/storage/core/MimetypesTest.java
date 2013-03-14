package com.snda.storage.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.snda.storage.core.FileTypes;

public class MimetypesTest {

	@Test
	public void testGetInstance() {
		FileTypes instance1 = FileTypes.getInstance();
		FileTypes instance2 = FileTypes.getInstance();
		assertEquals(instance1.hashCode(), instance2.hashCode());
	}

	@Test
	public void testGetMimetype() {
		assertEquals("text/plain", FileTypes.getInstance().getMimetype("a.txt"));
		assertEquals("application/pdf", FileTypes.getInstance().getMimetype("a.pdf"));
		assertEquals("application/octet-stream", FileTypes.getInstance().getMimetype("a.exe"));
		assertEquals("application/zip", FileTypes.getInstance().getMimetype("a.zip"));
		assertEquals("application/x-gzip", FileTypes.getInstance().getMimetype("a.gz"));
		assertEquals("audio/mpeg", FileTypes.getInstance().getMimetype("a.mp3"));
		assertEquals("video/mp4", FileTypes.getInstance().getMimetype("a.mp4"));
		assertEquals("application/msword", FileTypes.getInstance().getMimetype("a.doc"));
		assertEquals("application/vnd.ms-powerpoint", FileTypes.getInstance().getMimetype("a.ppt"));
	}

}
