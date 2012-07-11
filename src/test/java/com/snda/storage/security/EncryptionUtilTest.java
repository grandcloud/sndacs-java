package com.snda.storage.security;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Before;
import org.junit.Test;

public class EncryptionUtilTest {
	
	private static final String password = "szwdsd";
	private static EncryptionUtil ENCRYPTION_UTIL;
	
	@Before
	public void createEncryptionUtil() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		ENCRYPTION_UTIL = new EncryptionUtil(password);
	}

	@Test
	public void testEncrypt() throws InvalidKeyException,
			IllegalStateException, IllegalBlockSizeException,
			BadPaddingException, UnsupportedEncodingException,
			InvalidKeySpecException, InvalidAlgorithmParameterException,
			NoSuchAlgorithmException, NoSuchPaddingException {
		
		String word = "This sentence is what I want to encrypt.";
		
		byte[] encrypt = ENCRYPTION_UTIL.encrypt(word);
		String decryptString = ENCRYPTION_UTIL.decryptString(encrypt, 0, encrypt.length);
		
		assertEquals(word, decryptString);
	}

	@Test
	public void testGetAlgorithm() throws InvalidKeyException,
			NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException {
		assertEquals("PBEWithMD5AndDES", ENCRYPTION_UTIL.getAlgorithm());
		
		EncryptionUtil encryptionUtil = new EncryptionUtil(password, "PBE", "1");
		
		assertEquals("PBE", encryptionUtil.getAlgorithm());

		encryptionUtil = new EncryptionUtil(password, "PBKDF2WithHmacSHA1", "1");
		
		assertEquals("PBKDF2WithHmacSHA1", encryptionUtil.getAlgorithm());
		
	}

}
