package com.snda.storage.service.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.base.Joiner;
import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.service.Constants;
import com.snda.storage.service.model.CSObject;

/**
 * General utility methods used throughout the sdk project.
 * 
 * @author snda
 *
 */
public class ServiceUtils {
	private static final Logger log = LoggerFactory.getLogger(ServiceUtils.class);
	
	protected static final SimpleDateFormat iso8601DateParser = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	// The Eucalyptus Walrus storage service returns short, non-UTC date time
	// values.
	protected static final SimpleDateFormat iso8601DateParser_Walrus = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	protected static final SimpleDateFormat rfc822DateParser = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

	static {
		iso8601DateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
		rfc822DateParser.setTimeZone(new SimpleTimeZone(0, "GMT"));
	}
	
	public static Date parseIso8601Date(String dateString)
			throws ParseException {
		ParseException exception = null;
		synchronized (iso8601DateParser) {
			try {
				return iso8601DateParser.parse(dateString);
			} catch (ParseException e) {
				exception = e;
			}
		}
		// Work-around to parse datetime value returned by Walrus
		synchronized (iso8601DateParser_Walrus) {
			try {
				return iso8601DateParser_Walrus.parse(dateString);
			} catch (ParseException e) {
				// Ignore work-around exceptions
			}
		}
		// Throw original exception if the Walrus work-around doesn't save us.
		throw exception;
	}

	public static String formatIso8601Date(Date date) {
		synchronized (iso8601DateParser) {
			return iso8601DateParser.format(date);
		}
	}

	public static Date parseRfc822Date(String dateString) throws ParseException {
		synchronized (rfc822DateParser) {
			return rfc822DateParser.parse(dateString);
		}
	}

	public static String formatRfc822Date(Date date) {
		synchronized (rfc822DateParser) {
			return rfc822DateParser.format(date);
		}
	}
	
	/**
     * From a map of metadata returned from a REST GET or HEAD request, returns a map
     * of metadata with the HTTP-connection-specific metadata items removed.
     *
     * @param metadata
     * @return
     * metadata map with HTTP-connection-specific items removed.
     */
	public static Map<String, Object> cleanRestMetadataMap(
	        Map<String, Object> metadata, String headerPrefix, String metadataPrefix) {
		Map<String, Object> cleanMap = new HashMap<String, Object>();
		if (metadata != null) {
			for (Map.Entry<String, Object> entry: metadata.entrySet()) {
				String key = entry.getKey();
                Object value = entry.getValue();
                
                // Trim prefixes from keys.
                String keyStr = (key != null ? key.toString() : "");
                if (keyStr.startsWith(metadataPrefix)) {
                    key = keyStr
                        .substring(metadataPrefix.length(), keyStr.length());
                    if (log.isDebugEnabled()) {
                        log.debug("Removed meatadata header prefix "
                            + headerPrefix + " from key: " + keyStr + "=>" + key);
                    }
                } else if (keyStr.startsWith(headerPrefix)) {
                    key = keyStr.substring(headerPrefix.length(), keyStr.length());
                    if (log.isDebugEnabled()) {
                        log.debug("Removed SNDA header prefix "
                            + headerPrefix + " from key: " + keyStr + "=>" + key);
                    }
                } else if (RestUtils.HTTP_HEADER_METADATA_NAMES.contains(keyStr.toLowerCase(Locale.getDefault()))) {
                    key = keyStr;
                    if (log.isDebugEnabled()) {
                        log.debug("Leaving HTTP header item unchanged: " + key + "=" + value);
                    }
                } else if ("ETag".equalsIgnoreCase(keyStr)
                    || "Date".equalsIgnoreCase(keyStr)
                    || "Last-Modified".equalsIgnoreCase(keyStr)
                    || "Content-Range".equalsIgnoreCase(keyStr))
                {
                    key = keyStr;
                    if (log.isDebugEnabled()) {
                        log.debug("Leaving header item unchanged: " + key + "=" + value);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Ignoring metadata item: " + keyStr + "=" + value);
                    }
                    continue;
                }

                // Convert connection header string Collections into simple strings (where
                // appropriate)
                if (value instanceof Collection) {
                    Collection<?> coll = (Collection<?>) value;
                    if (coll.size() == 1) {
                        if (log.isDebugEnabled()) {
                            log.debug("Converted metadata single-item Collection "
                                + coll.getClass() + " " + coll + " for key: " + key);
                        }
                        value = coll.iterator().next();
                    } else {
                        if (log.isWarnEnabled()) {
                            log.warn("Collection " + coll
                                + " has too many items to convert to a single string");
                        }
                    }
                }

                // Parse date strings into Date objects, if necessary.
                if ("Date".equals(key) || "Last-Modified".equals(key)) {
                    if (!(value instanceof Date)) {
                        if (log.isDebugEnabled()) {
                            log.debug("Parsing date string '" + value
                            + "' into Date object for key: " + key);
                        }
                        try {
                            value = ServiceUtils.parseRfc822Date(value.toString());
                        } catch (ParseException pe) {
                            // Try ISO-8601 date format, just in case
                            try {
                                value = ServiceUtils.parseIso8601Date(value.toString());
                            } catch (ParseException pe2) {
                                // Log original exception if the work-around fails.
                                if (log.isWarnEnabled()) {
                                    log.warn("Date string is not RFC 822 compliant for metadata field " + key, pe);
                                }
                            }
                        }
                    }
                }

                cleanMap.put(key, value);
			}
		}
		
		return cleanMap;
	}

	/**
     * Calculate the HMAC/SHA1 on a string.
     *
     * @param csSecretKey
     * SNDA secret key.
     * @param canonicalString
     * canonical string representing the request to sign.
     * @return Signature
     * @throws ServiceException
     */
	public static String signWithHmacSha1(String csSecretKey, String canonicalString) throws ServiceException {
		if (csSecretKey == null) {
            return null;
        }
		
		SecretKeySpec signingKey = null;
        try {
            signingKey = new SecretKeySpec(csSecretKey.getBytes(Constants.DEFAULT_ENCODING),
                Constants.HMAC_SHA1_ALGORITHM);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to get bytes from secret string", e);
        }

        // Acquire the MAC instance and initialize with the signing key.
        Mac mac = null;
        try {
            mac = Mac.getInstance(Constants.HMAC_SHA1_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            // should not happen
            throw new RuntimeException("Could not find sha1 algorithm", e);
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            // also should not happen
            throw new RuntimeException("Could not initialize the MAC algorithm", e);
        }

        // Compute the HMAC on the digest, and set it.
        try {
            byte[] b64 = Base64.encodeBase64(mac.doFinal(
                canonicalString.getBytes(Constants.DEFAULT_ENCODING)));
            return new String(b64);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to get bytes from canonical string", e);
        }
	}
	
	/**
     * Reads text data from an input stream and returns it as a String.
     *
     * @param is
     * input stream from which text data is read.
     * @param encoding
     * the character encoding of the textual data in the input stream. If this
     * parameter is null, the default system encoding will be used.
     *
     * @return
     * text data read from the input stream.
     *
     * @throws IOException
     */
    public static String readInputStreamToString(InputStream is, String encoding) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        if (encoding != null) {
            br = new BufferedReader(new InputStreamReader(is, encoding));
        } else {
            br = new BufferedReader(new InputStreamReader(is));
        }
        String line = null;
        try {
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (!firstLine) {
                    sb.append("\n");
                }
                sb.append(line);
                firstLine = false;
            }
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to read String from Input Stream", e);
            }
        }
        return sb.toString();
    }
    
    /**
     * Reads from an input stream until a newline character or the end of the stream is reached.
     *
     * @param is
     * @return
     * text data read from the input stream, not including the newline character.
     * @throws IOException
     */
    public static String readInputStreamLineToString(InputStream is, String encoding) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = -1;
        while ((b = is.read()) != -1) {
            if ('\n' == (char) b) {
                break;
            } else {
                baos.write(b);
            }
        }
        return new String(baos.toByteArray(), encoding);
    }
    
    /**
     * Reads binary data from an input stream and returns it as a byte array.
     *
     * @param is
     * input stream from which data is read.
     *
     * @return
     * byte array containing data read from the input stream.
     *
     * @throws IOException
     */
    public static byte[] readInputStreamToBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b = -1;
        while ((b = is.read()) != -1) {
            baos.write(b);
        }
        return baos.toByteArray();
    }
	
    /**
     * Converts byte data to a Hex-encoded string.
     *
     * @param data
     * data to hex encode.
     * @return
     * hex-encoded string.
     */
	public static String toHex(byte[] data) {
        StringBuffer sb = new StringBuffer(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i]);
            if (hex.length() == 1) {
                // Append leading zero.
                sb.append("0");
            } else if (hex.length() == 8) {
                // Remove ff prefix from negative numbers.
                hex = hex.substring(6);
            }
            sb.append(hex);
        }
        return sb.toString().toLowerCase(Locale.getDefault());
    }
	
	/**
     * Converts a Hex-encoded data string to the original byte data.
     *
     * @param hexData
     * hex-encoded data to decode.
     * @return
     * decoded data from the hex string.
     */
	public static byte[] fromHex(String hexData) {
        if ((hexData.length() & 1) != 0  ||
            hexData.replaceAll("[a-fA-F0-9]", "").length() > 0) {
            throw new java.lang.IllegalArgumentException("'" + hexData + "' is not a hex string");
        }

        byte[] result = new byte[(hexData.length() + 1) / 2];
        String hexNumber = null;
        int stringOffset = 0;
        int byteOffset = 0;
        while (stringOffset < hexData.length()) {
            hexNumber = hexData.substring(stringOffset, stringOffset + 2);
            stringOffset += 2;
            result[byteOffset++] = (byte) Integer.parseInt(hexNumber, 16);
        }
        return result;
    }

	/**
     * Converts byte data to a Base64-encoded string.
     *
     * @param data
     * data to Base64 encode.
     * @return
     * encoded Base64 string.
     */
	public static String toBase64(byte[] data) {
        byte[] b64 = Base64.encodeBase64(data);
        return new String(b64);
    }
	
	/**
     * Converts a Base64-encoded string to the original byte data.
     *
     * @param b64Data
     * a Base64-encoded string to decode.
     *
     * @return
     * bytes decoded from a Base64 string.
     */
	public static byte[] fromBase64(String b64Data) {
        byte[] decoded = Base64.decodeBase64(b64Data.getBytes());
        return decoded;
    }
	
	/**
     * Computes the MD5 hash of the data in the given input stream and returns it as a hex string.
     * The provided input stream is consumed and closed by this method.
     *
     * @param is
     * @return
     * MD5 hash
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
	public static byte[] computeMD5Hash(InputStream is) throws NoSuchAlgorithmException, IOException {
        BufferedInputStream bis = new BufferedInputStream(is);
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[16384];
            int bytesRead = -1;
            while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
                messageDigest.update(buffer, 0, bytesRead);
            }
            return messageDigest.digest();
        } finally {
            try {
                bis.close();
            } catch (Exception e) {
            	log.error("Unable to close input stream of hash candidate: ", e);
//                System.err.println("Unable to close input stream of hash candidate: " + e);
            }
        }
    }
	
	/**
     * Computes the MD5 hash of the given data and returns it as a hex string.
     *
     * @param data
     * @return
     * MD5 hash.
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
	public static byte[] computeMD5Hash(byte[] data) throws NoSuchAlgorithmException, IOException {
        return computeMD5Hash(new ByteArrayInputStream(data));
    }
	
	public static boolean isEtagAlsoAnMD5Hash(String etag) {
        if (etag == null || etag.length() != 32) {
            return false;
        }
        String nonHexChars = etag.toLowerCase().replaceAll("[a-f0-9]", "");
        if (nonHexChars.length() > 0) {
            return false;
        }
        return true;
    }
	
	public static String generateCSHostnameForBucket(String bucketName,
			boolean isDnsBucketNamingDisabled, String csEndpoint) {
		return csEndpoint;
	}
	
	public static Joiner getJoiner(char combineSymbol) {
		return Joiner.on(combineSymbol).useForNull("");
	}
	
	public static Joiner getJoiner(String combineString) {
		return Joiner.on(combineString).useForNull("");
	}
	
	/**
     * Find a SAX XMLReader by hook or by crook, with work-arounds for
     * non-standard platforms.
     *
     * @return an initialized XML SAX reader
     */
	public static XMLReader loadXMLReader() {
        // Try loading the default SAX reader
        try {
            return XMLReaderFactory.createXMLReader();
        } catch (SAXException e) {
            // Ignore failure
        }

        // No dice using the standard approach, try loading alternatives...
        String[] altXmlReaderClasspaths = new String[] {
            "org.apache.crimson.parser.XMLReaderImpl",  // JDK 1.4
            "org.xmlpull.v1.sax2.Driver",  // Android
        };
        for (int i = 0; i < altXmlReaderClasspaths.length; i++) {
            String xmlReaderClasspath = altXmlReaderClasspaths[i];
            try {
                return XMLReaderFactory.createXMLReader(xmlReaderClasspath);
            } catch (SAXException e) {
                // Ignore failure
            }
        }
        // If we haven't found and returned an XMLReader yet, give up.
        throw new ServiceException("Failed to initialize a SAX XMLReader");
    }
	
	/**
     * Builds an object based on the bucket name and object key information
     * available in the components of a URL.
     *
     * @param host
     * the host name component of a URL that may include the bucket name,
     * if an alternative host name is in use.
     * @param urlPath
     * the path of a URL that references an CS object, and which may or may
     * not include the bucket name.
     *
     * @return
     * the object referred to by the URL components.
     */
    public static CSObject buildObjectFromUrl(String host, String urlPath, String csEndpoint)
        throws UnsupportedEncodingException
    {
        if (urlPath.startsWith("/")) {
            urlPath = urlPath.substring(1); // Ignore first '/' character in url path.
        }

        String bucketName = null;
        String objectKey = null;

        if (!csEndpoint.equals(host)) {
            bucketName = findBucketNameInHostname(host, csEndpoint);
        } else {
            // Bucket name must be first component of URL path
            int slashIndex = urlPath.indexOf("/");
            bucketName = URLDecoder.decode(
                urlPath.substring(0, slashIndex), Constants.DEFAULT_ENCODING);

            // Remove the bucket name component of the host name
            urlPath = urlPath.substring(bucketName.length() + 1);
        }

        objectKey = URLDecoder.decode(
            urlPath, Constants.DEFAULT_ENCODING);

        CSObject object = new CSObject(objectKey);
        object.setBucketName(bucketName);
        return object;
    }
    
    /**
     * Identifies the name of a bucket from a given host name, if available.
     * Returns null if the bucket name cannot be identified, as might happen
     * when a bucket name is represented by the path component of a URL instead
     * of the host name component.
     *
     * @param host
     * the host name component of a URL that may include the bucket name,
     * if an alternative host name is in use.
     *
     * @return
     * The CS bucket name represented by the DNS host name, or null if none.
     */
    public static String findBucketNameInHostname(String host, String csEndpoint) {
        String bucketName = null;
        // Bucket name is available in URL's host name.
        if (host.endsWith(csEndpoint)) {
            bucketName = host.substring(0,
                host.length() - csEndpoint.length() - 1);
        } else {
            // URL refers to a virtual host name
            bucketName = host;
        }
        return bucketName;
    }
    
}
