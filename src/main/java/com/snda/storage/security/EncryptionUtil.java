package com.snda.storage.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.service.Constants;

public class EncryptionUtil {
	private static final Logger log = LoggerFactory.getLogger(EncryptionUtil.class);

	public static final String DEFAULT_VERSION = "1";
	public static final String DEFAULT_ALGORITHM = "PBEWithMD5AndDES";
	
	private String algorithm = null;
    private String version = null;
    private SecretKey key = null;
    private AlgorithmParameterSpec algParamSpec = null;

    int ITERATION_COUNT = 5000;
    byte[] salt = {
        (byte)0xA4, (byte)0x0B, (byte)0xC8, (byte)0x34,
        (byte)0xD6, (byte)0x95, (byte)0xF3, (byte)0x13
    };

    static {
        try {
            Class bouncyCastleProviderClass =
                Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            if (bouncyCastleProviderClass != null) {
                Provider bouncyCastleProvider = (Provider) bouncyCastleProviderClass
                    .getConstructor(new Class[] {}).newInstance(new Object[] {});
                Security.addProvider(bouncyCastleProvider);
            }
            if (log.isDebugEnabled()) {
                log.debug("Loaded security provider BouncyCastleProvider");
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Unable to load security provider BouncyCastleProvider");
            }
        }
    }
	
	/**
     * Constructs class configured with the provided password, and set up to use the encryption
     * method specified.
     *
     * @param encryptionKey
     *        the password to use for encryption/decryption.
     * @param algorithm
     *        the Java name of an encryption algorithm to use, eg PBEWithMD5AndDES
     * @param version
     *        the version of encyption to use, for historic and future compatibility.
     *        Unless using an historic version, this should always be
     *        {@link #DEFAULT_VERSION}
     *
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public EncryptionUtil(String encryptionKey, String algorithm, String version) throws
        InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException
    {
        this.algorithm = algorithm;
        this.version = version;
        if (log.isDebugEnabled()) {
            log.debug("Cryptographic properties: algorithm=" + this.algorithm + ", version=" + this.version);
        }

        if (!DEFAULT_VERSION.equals(version)) {
            throw new RuntimeException("Unrecognised crypto version setting: " + version);
        }

        PBEKeySpec keyspec = new PBEKeySpec(encryptionKey.toCharArray(), salt, ITERATION_COUNT, 32);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm);
        key = skf.generateSecret(keyspec);
        algParamSpec = new PBEParameterSpec(salt, ITERATION_COUNT);
    }
    
    /**
     * Constructs class configured with the provided password, and set up to use the default encryption
     * algorithm PBEWithMD5AndDES.
     *
     * @param encryptionKey
     *        the password to use for encryption/decryption.
     *
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeySpecException
     */
    public EncryptionUtil(String encryptionKey) throws InvalidKeyException,
        NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException
    {
        this(encryptionKey, "PBEWithMD5AndDES", DEFAULT_VERSION);
    }
    
	protected Cipher initEncryptModeCipher() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.ENCRYPT_MODE, key, algParamSpec);
		return cipher;
	}

	protected Cipher initDecryptModeCipher() throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException,
			InvalidAlgorithmParameterException {
		Cipher cipher = Cipher.getInstance(algorithm);
		cipher.init(Cipher.DECRYPT_MODE, key, algParamSpec);
		return cipher;
	}
    
    /**
     * Encrypts a UTF-8 string to byte data.
     *
     * @param data
     * data to encrypt.
     * @return
     * encrypted data.
     *
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws UnsupportedEncodingException
     * @throws InvalidKeySpecException
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public byte[] encrypt(String data) throws IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, UnsupportedEncodingException, InvalidKeySpecException,
        InvalidKeyException, InvalidAlgorithmParameterException,
        NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return cipher.doFinal(data.getBytes(Constants.DEFAULT_ENCODING));
    }
    
    /**
     * Decrypts byte data to a UTF-8 string.
     *
     * @param data
     * data to decrypt.
     * @return
     * UTF-8 string of decrypted data.
     *
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String decryptString(byte[] data) throws InvalidKeyException,
        InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalStateException,
        IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initEncryptModeCipher();
        return new String(cipher.doFinal(data), Constants.DEFAULT_ENCODING);
    }

    /**
     * Decrypts a UTF-8 string.
     *
     * @param data
     * data to decrypt.
     * @param startIndex
     * start index of data to decrypt.
     * @param endIndex
     * end index of data to decrypt.
     * @return
     * UTF-8 string of decrypted data.
     *
     * @throws InvalidKeyException
     * @throws InvalidAlgorithmParameterException
     * @throws UnsupportedEncodingException
     * @throws IllegalStateException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     */
    public String decryptString(byte[] data, int startIndex, int endIndex)
        throws InvalidKeyException, InvalidAlgorithmParameterException,
        UnsupportedEncodingException, IllegalStateException, IllegalBlockSizeException,
        BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException
    {
        Cipher cipher = initDecryptModeCipher();
        return new String(cipher.doFinal(data, startIndex, endIndex), Constants.DEFAULT_ENCODING);
    }
    
    /**
     * @return
     * the Java name of the cipher algorithm being used by this class.
     */
    public String getAlgorithm() {
        return algorithm;
    }
}
