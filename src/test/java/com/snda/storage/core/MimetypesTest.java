package com.snda.storage.core;

import static org.junit.Assert.*;

import org.junit.Test;

import com.snda.storage.core.Mimetypes;

public class MimetypesTest {

	@Test
	public void testGetInstance() {
		Mimetypes instance1 = Mimetypes.getInstance();
		Mimetypes instance2 = Mimetypes.getInstance();
		assertEquals(instance1.hashCode(), instance2.hashCode());
	}

	@Test
	public void testGetMimetype() {
		assertEquals("text/plain", Mimetypes.getInstance().getMimetype("a.txt"));
		assertEquals("application/pdf", Mimetypes.getInstance().getMimetype("a.pdf"));
		assertEquals("application/octet-stream", Mimetypes.getInstance().getMimetype("a.exe"));
		assertEquals("application/zip", Mimetypes.getInstance().getMimetype("a.zip"));
		assertEquals("application/x-gzip", Mimetypes.getInstance().getMimetype("a.gz"));
		assertEquals("audio/mpeg", Mimetypes.getInstance().getMimetype("a.mp3"));
		assertEquals("video/mp4", Mimetypes.getInstance().getMimetype("a.mp4"));
		assertEquals("application/msword", Mimetypes.getInstance().getMimetype("a.doc"));
		assertEquals("application/vnd.ms-powerpoint", Mimetypes.getInstance().getMimetype("a.ppt"));
	}

}
