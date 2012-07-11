package com.snda.storage.security;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;

public class SNDACredentialsTest {
	
	private static final String accessKey = UUID.randomUUID().toString();
	private static final String secretKey = UUID.randomUUID().toString();
	private static final String algorithm = "PBEWithMD5AndDES";
	private static final String password = "szwdsd";
	private static ProviderCredentials pc;
	
	@Before
	public void createProviderCredentials() {
		pc = new SNDACredentials(accessKey, secretKey);
	}

	@Test
	public void testGetAccessKey() {
		assertEquals(accessKey, pc.getAccessKey());
	}

	@Test
	public void testGetSecretKey() {
		assertEquals(secretKey, pc.getSecretKey());
	}

	@Test
	public void testGetLogString() {
		assertEquals(accessKey + " : " + secretKey, pc.getLogString());
	}

	@Test
	public void testGetDataToEncrypt() {
		assertEquals(accessKey + "\n" + secretKey, pc.getDataToEncrypt());
	}

	@Test
	public void testSaveAndLoad() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IllegalStateException,
			IllegalBlockSizeException, BadPaddingException,
			InvalidAlgorithmParameterException, IOException {
		String filename = UUID.randomUUID().toString();
		File file = new java.io.File(filename);
		
		String content = UUID.randomUUID().toString();
		FileOutputStream fout = new FileOutputStream(file);
		fout.write(content.getBytes());
		fout.flush();
		fout.close();
		
		pc.save(password, file, algorithm);
		
		ProviderCredentials loadpc = ProviderCredentials.load(password, file);
		
		assertEquals(pc.getAccessKey(), loadpc.getAccessKey());
		assertEquals(pc.getSecretKey(), loadpc.getSecretKey());
		assertEquals(pc.getVersionPrefix(), loadpc.getVersionPrefix());
		assertEquals(pc.getDataToEncrypt(), loadpc.getDataToEncrypt());
		assertEquals(pc.getTypeName(), loadpc.getTypeName());
		assertEquals(pc.getLogString(), loadpc.getLogString());
		
		file.delete();
	}

}
