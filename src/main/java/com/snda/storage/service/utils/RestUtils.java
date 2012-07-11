package com.snda.storage.service.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.service.Constants;

/**
 * Utilities useful for REST/HTTP CSService implementations.
 * 
 * @author snda
 *
 */
public class RestUtils {

	private static final Logger log = LoggerFactory.getLogger(RestUtils.class);
	/**
     * A list of HTTP-specific header names, that may be present in CSObjects as metadata but
     * which should be treated as plain HTTP headers during transmission (ie not converted into
     * Cloud Storage Object metadata items). All items in this list are in lower case.
     * <p>
     * This list includes the items:
     * <table>
     * <tr><th>Unchanged metadata names</th></tr>
     * <tr><td>content-type</td></tr>
     * <tr><td>content-md5</td></tr>
     * <tr><td>content-length</td></tr>
     * <tr><td>content-language</td></tr>
     * <tr><td>expires</td></tr>
     * <tr><td>cache-control</td></tr>
     * <tr><td>content-disposition</td></tr>
     * <tr><td>content-encoding</td></tr>
     * </table>
     */
	public static final List<String> HTTP_HEADER_METADATA_NAMES = Arrays.asList(new String[] {
	        "content-type",
	        "content-md5",
	        "content-length",
	        "content-language",
	        "expires",
	        "cache-control",
	        "content-disposition",
	        "content-encoding"
	        });

	/**
     * Encodes a URL string, and ensures that spaces are encoded as "%20" instead of "+" to keep
     * fussy web browsers happier.
     *
     * @param path
     * @return
     * encoded URL.
     * @throws ServiceException
     */
	public static String encodeUrlString(String path) {
        try {
            String encodedPath = URLEncoder.encode(path, Constants.DEFAULT_ENCODING);
            // Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
            encodedPath = encodedPath.replaceAll("\\+", "%20");
            // '@' character need not be URL encoded and Google Chrome balks on signed URLs if it is.
            encodedPath = encodedPath.replaceAll("%40", "@");
            return encodedPath;
        } catch (UnsupportedEncodingException uee) {
        	log.error("Occurred an UnsupportedEncodingException while encoding url string.", uee);
            throw new ServiceException("Unable to encode path: " + path, uee);
        }
    }
	
	/**
     * Encodes a URL string but leaves a delimiter string unencoded.
     * Spaces are encoded as "%20" instead of "+".
     *
     * @param path
     * @param delimiter
     * @return
     * encoded URL string.
     * @throws ServiceException
     */
	public static String encodeUrlPath(String path, String delimiter) {
        StringBuffer result = new StringBuffer();
        String tokens[] = path.split(delimiter);
        for (int i = 0; i < tokens.length; i++) {
            result.append(encodeUrlString(tokens[i]));
            if (i < tokens.length - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }
	
	/**
	 * Initialized HTTP connection
	 * @return 
	 * HttpClient object
	 */
	public static HttpClient initHttpConnection() {
		ClientConnectionManager conman = new ThreadSafeClientConnManager();
		HttpParams params = new BasicHttpParams();
		DefaultHttpClient httpClient = new DefaultHttpClient(conman, params);
		httpClient.setRedirectStrategy(new DefaultRedirectStrategy());
		return httpClient;
	}
	
	public static Map<String, String> convertHeadersToMap(Header[] headers) {
        Map<String, String> csHeaders = new HashMap<String, String>();
        for (Header header: headers) {
            csHeaders.put(header.getName(), header.getValue());
        }
        return csHeaders;
    }
	
	/**
     * Calculate the canonical string for a REST/HTTP request to a storage service.
     *
     * When expires is non-null, it will be used instead of the Date header.
     * @throws UnsupportedEncodingException
     */
	public static String makeServiceCanonicalString(String method, String resource,
	        Map<String, Object> headersMap, String expires, String headerPrefix,
	        List<String> serviceResourceParameterNames) throws UnsupportedEncodingException
	    {
	        StringBuilder canonicalStringBuf = new StringBuilder();
	        canonicalStringBuf.append(method + "\n");

	        SortedMap<String, Object> interestingHeaders = new TreeMap<String, Object>();
	        if (headersMap != null && headersMap.size() > 0) {
	            for (Map.Entry<String, Object> entry: headersMap.entrySet()) {
	                Object key = entry.getKey();
	                Object value = entry.getValue();

	                if (key == null) {
	                    continue;
	                }
	                String lk = key.toString().toLowerCase(Locale.getDefault());

	                // Ignore any headers that are not particularly interesting.
	                if (lk.equals("content-type") || lk.equals("content-md5") || lk.equals("date") ||
	                    lk.startsWith(headerPrefix))
	                {
	                    interestingHeaders.put(lk, value);
	                }
	            }
	        }

	        if (interestingHeaders.containsKey(Constants.REST_METADATA_ALTERNATE_DATE)) {
	            interestingHeaders.put("date", "");
	        }

	        if (expires != null) {
	            interestingHeaders.put("date", expires);
	        }

	        // these headers require that we still put a new line in after them,
	        // even if they don't exist.
	        if (! interestingHeaders.containsKey("content-type")) {
	            interestingHeaders.put("content-type", "");
	        }
	        if (! interestingHeaders.containsKey("content-md5")) {
	            interestingHeaders.put("content-md5", "");
	        }

	        for (Map.Entry<String, Object> entry: interestingHeaders.entrySet()) {
	            String key = entry.getKey();
	            Object value = entry.getValue();

	            if (key.startsWith(headerPrefix)) {
	                canonicalStringBuf.append(key).append(':').append(value);
	            } else {
	                canonicalStringBuf.append(value);
	            }
	            canonicalStringBuf.append("\n");
	        }

	        // don't include the query parameters...
	        int queryIndex = resource.indexOf('?');
	        if (queryIndex == -1) {
	            canonicalStringBuf.append(resource);
	        } else {
	            canonicalStringBuf.append(resource.substring(0, queryIndex));
	        }

	        // ...unless the parameter(s) are in the set of special params
	        // that actually identify a service resource.
	        if (queryIndex >= 0) {
	            SortedMap<String, String> sortedResourceParams = new TreeMap<String, String>();

	            // Parse parameters from resource string
	            String query = resource.substring(queryIndex + 1);
	            for (String paramPair: query.split("&")) {
	                String[] paramNameValue = paramPair.split("=");
	                String name = URLDecoder.decode(paramNameValue[0], "UTF-8");
	                String value = null;
	                if (paramNameValue.length > 1) {
	                    value = URLDecoder.decode(paramNameValue[1], "UTF-8");
	                }
	                // Only include parameter (and its value if present) in canonical
	                // string if it is a resource-identifying parameter
	                if (serviceResourceParameterNames.contains(name)) {
	                    sortedResourceParams.put(name, value);
	                }
	            }

	            // Add resource parameters
	            if (sortedResourceParams.size() > 0) {
	                canonicalStringBuf.append("?");
	            }
	            boolean addedParam = false;
	            for (Map.Entry<String, String> entry: sortedResourceParams.entrySet()) {
	                if (addedParam) {
	                    canonicalStringBuf.append("&");
	                }
	                canonicalStringBuf.append(entry.getKey());
	                if (entry.getValue() != null) {
	                    canonicalStringBuf.append("=" + entry.getValue());
	                }
	                addedParam = true;
	            }
	        }

	        return canonicalStringBuf.toString();
	    }
}
