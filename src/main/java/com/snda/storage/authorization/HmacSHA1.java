package com.snda.storage.authorization;
import static com.google.common.base.Preconditions.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.google.common.base.Charsets;

/**
 * 
 * @author shenjiong
 * 
 */
public class HmacSHA1 {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	private HmacSHA1() {
	}

	public static String calculate(String secretAccessKey, String data) {
		checkNotNull(secretAccessKey);
		checkNotNull(data);
		try {
			SecretKeySpec signingKey = new SecretKeySpec(secretAccessKey.getBytes(Charsets.UTF_8), HMAC_SHA1_ALGORITHM);

			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			byte[] rawHmac = mac.doFinal(data.getBytes(Charsets.UTF_8));
			return new String(Base64.encodeBase64(rawHmac), Charsets.UTF_8);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		} catch (InvalidKeyException e) {
			throw new IllegalStateException(e);
		}
	}

}
