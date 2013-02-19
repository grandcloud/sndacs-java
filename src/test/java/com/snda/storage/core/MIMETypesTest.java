package com.snda.storage.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MIMETypesTest {

	@Test
	public void testGetMimetype() {
		assertEquals("text/plain", MIMETypes.getInstance().getMimetype("a.txt"));
		assertEquals("application/pdf", MIMETypes.getInstance().getMimetype("a.pdf"));
		assertEquals("application/octet-stream", MIMETypes.getInstance().getMimetype("a.exe"));
		assertEquals("application/zip", MIMETypes.getInstance().getMimetype("a.zip"));
		assertEquals("application/x-gzip", MIMETypes.getInstance().getMimetype("a.gz"));
		assertEquals("audio/mpeg", MIMETypes.getInstance().getMimetype("a.mp3"));
		assertEquals("video/mp4", MIMETypes.getInstance().getMimetype("a.mp4"));
		assertEquals("application/msword", MIMETypes.getInstance().getMimetype("a.doc"));
		assertEquals("application/vnd.ms-powerpoint", MIMETypes.getInstance().getMimetype("a.ppt"));
	}

}
