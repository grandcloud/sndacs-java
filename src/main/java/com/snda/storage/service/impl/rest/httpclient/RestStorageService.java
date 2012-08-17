package com.snda.storage.service.impl.rest.httpclient;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.jamesmurty.utils.XMLBuilder;
import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.service.CSRequestAuthorizer;
import com.snda.storage.service.Constants;
import com.snda.storage.service.StorageObjectsChunk;
import com.snda.storage.service.StorageService;
import com.snda.storage.service.model.CSBucket;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.CreateBucketConfiguration;
import com.snda.storage.service.model.StorageBucket;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.utils.Mimetypes;
import com.snda.storage.service.utils.RestUtils;
import com.snda.storage.service.utils.ServiceUtils;
import com.snda.storage.service.utils.XmlResponsesSaxParser.CopyObjectResultHandler;
import com.snda.storage.service.utils.XmlResponsesSaxParser.ListBucketHandler;

/**
 * Abstract REST/HTTP implementation of an CSService based on the
 * <a href="http://jakarta.apache.org/commons/httpclient/">HttpClient</a> library.
 * <p>
 * 
 * @author snda
 *
 */
public abstract class RestStorageService extends StorageService implements CSRequestAuthorizer {
	
	private static final Logger log = LoggerFactory.getLogger(RestStorageService.class);

	protected static final Joiner VIRGULE_JOINER = ServiceUtils.getJoiner(Constants.VIRGULE);
	protected static final Joiner SPACE_JOINER = ServiceUtils.getJoiner(Constants.SPACE);

	protected static enum HTTP_METHOD {PUT, POST, HEAD, GET, DELETE};
	
	protected HttpClient httpClient = null;
	private String defaultStorageClass = "STANDARD";

	/**
     * Constructs the service and initializes the properties.
     *
     * @param credentials
     * the user credentials to use when communicating with Cloud Storage.
     */
	public RestStorageService(ProviderCredentials credentials) {
		super(credentials);
		this.httpClient = RestUtils.initHttpConnection();
	}
	
	/**
     * @return
     * the HTTP client for this service.
     */
	public HttpClient getHttpClient() {
        return this.httpClient;
    }
	
	/**
     * Replaces the service's default HTTP client.
     * This method should only be used by advanced users.
     *
     * @param httpClient
     * the client that will replace the default client created by
     * the class constructor.
     */
	public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
	
	/**
     * Adds the provided request headers to the connection.
     *
     * @param httpMethod
     *        the connection object
     * @param requestHeaders
     *        the request headers to add as name/value pairs.
     */
	protected void addRequestHeadersToConnection(
			HttpRequestBase httpMethod, Map<String, Object> requestHeaders)
    {
        if (requestHeaders != null) {
            for (Map.Entry<String, Object> entry: requestHeaders.entrySet()) {
                String key = entry.getKey().toString();
                String value = entry.getValue().toString();
                
                httpMethod.addHeader(key, value);
            }
        }
    }
	
	/**
     * Converts an array of Header objects to a map of name/value pairs.
     *
     * @param headers
     * @return
     */
	private Map<String, Object> convertHeadersToMap(Header[] headers) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (int i = 0; headers != null && i < headers.length; i++) {
            map.put(headers[i].getName(), headers[i].getValue());
        }
        return map;
    }
	
	/**
     * Creates an {@link org.apache.http.client.methods.HttpRequestBase} object to handle a particular connection method.
     *
     * @param method
     *        the HTTP method/connection-type to use, must be one of: PUT, HEAD, GET, DELETE
     * @param bucketName
     *        the bucket's name
     * @param objectKey
     *        the object's key name, may be null if the operation is on a bucket only.
     * @return
     *        the HttpRequestBase object used to perform the request
     */
	private HttpRequestBase setupConnection(HTTP_METHOD method,
			String bucketName, String objectKey,
			Map<String, String> requestParameters) {
		String url = null;
		
		String protocol = null;
		int port;
		if (isHttpsOnly()) {
			protocol = "https://";
			port = getHttpsPort();
		} else {
			protocol = "http://";
			port = getHttpPort();
		}
		
		if (bucketName != null && bucketName.equals("")) {
			url = protocol + getEndpoint() + ":" + port + Constants.VIRGULE;
		} else if (bucketName != null && objectKey == null) {
			url = protocol + VIRGULE_JOINER.join(getEndpoint() + ":" + port,bucketName);
		} else {
			url = protocol + VIRGULE_JOINER.join(
					getEndpoint() + ":" + port,
					bucketName,
					(objectKey != null? RestUtils.encodeUrlString(objectKey) : ""));
		}
		
		// Add additional request parameters to the URL for special cases (eg ACL operations)
        url = addRequestParametersToUrlPath(url, requestParameters);
		
		HttpRequestBase httpMethod = null;
		if (HTTP_METHOD.HEAD.equals(method)) {
			httpMethod = new HttpHead(url);
		} else if (HTTP_METHOD.PUT.equals(method)) {
			httpMethod = new HttpPut(url);
		} else if (HTTP_METHOD.POST.equals(method)) {
			httpMethod = new HttpPost(url);
		} else if (HTTP_METHOD.GET.equals(method)) {
			httpMethod = new HttpGet(url);
		} else if (HTTP_METHOD.DELETE.equals(method)) {
			httpMethod = new HttpDelete(url);
		} else {
			throw new IllegalArgumentException("Unrecognised HTTP method name: " + method);
		}
		
		return httpMethod;
	}
	
	private void releaseConnection(HttpResponse pResponse){
        if (pResponse == null){
            return;
        }
        try {
            EntityUtils.consume(pResponse.getEntity());
        } catch (Exception e){
            log.warn("Unable to consume response entity " + pResponse, e);
        }
    }
	
	/**
     * Performs an HTTP HEAD request using the {@link #performRequest} method.
     *
     * @param bucketName
     *        the bucket's name
     * @param objectKey
     *        the object's key name, may be null if the operation is on a bucket only.
     * @param requestParameters
     *        parameters to add to the request URL as GET params
     * @param requestHeaders
     *        headers to add to the request
     * @return
     *        the HttpResponse object
     */
	protected HttpResponse performRestHead(String bucketName, String objectKey,
			Map<String, String> requestParameters,
			Map<String, Object> requestHeaders) {
		HttpRequestBase httpMethod = setupConnection(HTTP_METHOD.HEAD,
				bucketName, objectKey, requestParameters);

		// Add all request headers.
		addRequestHeadersToConnection(httpMethod, requestHeaders);

		return performRequest(httpMethod, new int[] { 200 });
	}

	/**
     * Performs an HTTP GET request using the {@link #performRequest} method.
     *
     * @param bucketName
     *        the bucket's name
     * @param objectKey
     *        the object's key name, may be null if the operation is on a bucket only.
     * @param requestParameters
     *        parameters to add to the request URL as GET params
     * @param requestHeaders
     *        headers to add to the request
     * @return
     *        The HttpResponse object.
     */
	protected HttpResponse performRestGet(String bucketName, String objectKey,
			Map<String, String> requestParameters,
			Map<String, Object> requestHeaders) {
		HttpRequestBase httpMethod = setupConnection(HTTP_METHOD.GET,
				bucketName, objectKey, requestParameters);

		// Add all request headers.
		addRequestHeadersToConnection(httpMethod, requestHeaders);

		int[] expectedStatusCodes = { 200 }; // 200 is normally the expected
												// response code
		if (requestHeaders != null && requestHeaders.containsKey("Range")) {
			// Partial data responses have a status code of 206, or sometimes
			// 200
			expectedStatusCodes = new int[] { 206, 200 };
		}
		return performRequest(httpMethod, expectedStatusCodes);
	}
	
	/**
     * Performs an HTTP PUT request using the {@link #performRequest} method.
     *
     * @param bucketName
     *        the name of the bucket the object will be stored in.
     * @param objectKey
     *        the key (name) of the object to be stored.
     * @param metadata
     *        map of name/value pairs to add as metadata to any Cloud Storage objects created.
     * @param requestParameters
     *        parameters to add to the request URL as GET params
     * @param requestEntity
     *        an HttpClient object that encapsulates the object and data contents that will be
     *        uploaded. This object supports the resending of object data, when possible.
     * @param autoRelease
     *        if true, the HTTP Method object will be released after the request has
     *        completed and the connection will be closed. If false, the object will
     *        not be released and the caller must take responsibility for doing this.
     * @return
     *        The HttpResponse object.
     */
	protected HttpResponse performRestPut(String bucketName, String objectKey,
			Map<String, Object> metadata,
			Map<String, String> requestParameters, HttpEntity requestEntity,
			boolean autoRelease) {
		// Add any request parameters.
		HttpRequestBase httpMethod = setupConnection(HTTP_METHOD.PUT,
				bucketName, objectKey, requestParameters);

		Map<String, Object> renamedMetadata = renameMetadataKeys(metadata);
		addMetadataToHeaders(httpMethod, renamedMetadata);

		((HttpPut) httpMethod).setEntity(requestEntity);

		return performRequest(httpMethod, new int[] { 200, 204 });
	}
	
	/**
     * Performs an HTTP POST request using the {@link #performRequest} method.
     *
     * @param bucketName
     * the name of the bucket the object will be stored in.
     * @param objectKey
     * the key (name) of the object to be stored.
     * @param metadata
     * map of name/value pairs to add as metadata to any CS objects created.
     * @param requestParameters
     * parameters to add to the request URL as GET params
     * @param requestEntity
     * an HttpClient object that encapsulates the object and data contents that will be
     * uploaded. This object supports the re-sending of object data, when possible.
     * @param autoRelease
     * if true, the HTTP Method object will be released after the request has
     * completed and the connection will be closed. If false, the object will
     * not be released and the caller must take responsibility for doing this.
     * @return
     * a package including the HTTP method object used to perform the request, and the
     * content length (in bytes) of the object that was POSTed to CS.
     *
     */
    protected HttpResponse performRestPost(String bucketName, String objectKey,
        Map<String, Object> metadata, Map<String, String> requestParameters,
        HttpEntity requestEntity, boolean autoRelease)
        throws ServiceException
    {
        // Add any request parameters.
        HttpRequestBase postMethod = setupConnection(HTTP_METHOD.POST,
				bucketName, objectKey, requestParameters);

        Map<String, Object> renamedMetadata = renameMetadataKeys(metadata);
        addMetadataToHeaders(postMethod, renamedMetadata);

        if (requestEntity != null) {
            ((HttpPost)postMethod).setEntity(requestEntity);
        }

        HttpResponse result = performRequest(postMethod, new int[] {200, 201});

        if (autoRelease) {
            releaseConnection(result);
        }

        return result;
    }
	
	/**
     * Performs an HTTP DELETE request using the {@link #performRequest} method.
     *
     * @param bucketName
     * the bucket's name
     * @param objectKey
     * the object's key name, may be null if the operation is on a bucket only.
     * @return
     * The HttpResponse object.
     */
	protected HttpResponse performRestDelete(String bucketName,
			String objectKey, Map<String, String> requestParameters) {
		HttpRequestBase httpMethod = setupConnection(HTTP_METHOD.DELETE,
				bucketName, objectKey, requestParameters);

		return performRequest(httpMethod, new int[] { 200, 204 });
	}
	
	protected HttpResponse performRestPostWithXmlBuilder(String bucketName,
			String objectKey, Map<String, Object> metadata, Map<String, String> requestParameters,
			XMLBuilder builder) {
		try {
			if (metadata == null) {
				metadata = new HashMap<String, Object>();
			}
			if (!metadata.containsKey("content-type")) {
				metadata.put("Content-Type", "text/plain");
			}
			String xml = builder.asString(null);
			return performRestPost(bucketName, objectKey, metadata, requestParameters,
					new StringEntity(xml, "text/plain", Constants.DEFAULT_ENCODING), false);
		} catch (Exception e) {
			if (e instanceof ServiceException) {
				throw (ServiceException) e;
			} else {
				throw new ServiceException("Failed to POST request containing an XML document", e);
			}
		}
	}
	
	/**
     * Performs an HTTP/S request by invoking the provided HttpMethod object. If the HTTP
     * response code doesn't match the expected value, an exception is thrown.
     *
     * @param httpMethod
     *        the object containing a request target and all other information necessary to perform the
     *        request
     * @param expectedResponseCodes
     *        the HTTP response code(s) that indicates a successful request. If the response code received
     *        does not match this value an error must have occurred, so an exception is thrown.
     */
	private HttpResponse performRequest(HttpRequestBase httpMethod, int[] expectedResponseCodes) {
		try {
			authorizeHttpRequest(httpMethod);
			HttpResponse response = httpClient.execute(httpMethod);
			if (httpMethod.getMethod().equals(HttpHead.METHOD_NAME)) {
				return response;
			}
			int status = response.getStatusLine().getStatusCode();
			if (httpMethod.getMethod().equalsIgnoreCase(HttpDelete.METHOD_NAME.toLowerCase())
					&& status == 301) {
				DefaultHttpClient dclient = (DefaultHttpClient) httpClient;
				HttpUriRequest redirectRequest = dclient
						.getRedirectStrategy()
						.getRedirect(httpMethod, response, createHttpContext(dclient));
				httpMethod.setURI(redirectRequest.getURI());
				response = dclient.execute(httpMethod);
				status = response.getStatusLine().getStatusCode();
			}
			Arrays.sort(expectedResponseCodes);
			if (Arrays.binarySearch(expectedResponseCodes, status) < 0) {
				StringBuilder sb = new StringBuilder();
				if (response.getEntity() != null) {
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					String line = null;
					while ((line = br.readLine()) != null ) {
						sb.append(line);
					}
				}
				ServiceException exception = new ServiceException(SPACE_JOINER.join("CS Error Message:",
																	httpMethod.getMethod(),
																	httpMethod.getURI().getPath(),
																	status,
																	response.getStatusLine().getReasonPhrase())
																	, sb.toString());
				exception.setRequestHost(httpMethod.getURI().getHost());
				exception.setRequestPath(httpMethod.getURI().getPath());
				exception.setResponseDate(response.getFirstHeader(Constants.REST_HEADER_DATE).getValue());
				exception.setResponseHeaders(response.getAllHeaders());
				exception.setRequestVerb(httpMethod.getMethod());
				exception.setResponseCode(status);
				exception.setResponseStatus(response.getStatusLine().getReasonPhrase());
				throw exception;
			}
			return response;
		} catch (IOException e) {
			log.error("Occurred an IOException while performing request .", e);
			throw new ServiceException(e);
		} catch (ProtocolException e) {
			log.error("Occurred an ProtocolException while performing request while redirect .", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
     * Adds all valid metadata name and value pairs as HTTP headers to the given HTTP method.
     * Null metadata names are ignored, as are metadata values that are not of type string.
     * <p>
     * The metadata values are verified to ensure that keys contain only ASCII characters,
     * and that items are not accidentally duplicated due to use of different capitalization.
     *
     * @param httpMethod
     * @param metadata
     */
	protected void addMetadataToHeaders(HttpRequestBase httpMethod,
			Map<String, Object> metadata) {
		Map<String, Object> headersAlreadySeenMap = new HashMap<String, Object>(
				metadata.size());

		for (Map.Entry<String, Object> entry : metadata.entrySet()) {
			String key = entry.getKey();
			Object objValue = entry.getValue();

			if (key == null) {
				// Ignore invalid metadata.
				continue;
			}

			String value = objValue.toString();

			// Ensure user-supplied metadata values are compatible with the REST
			// interface.
			// Key must be ASCII text, non-ASCII characters are not allowed in
			// HTTP header names.
			boolean validAscii = false;
			UnsupportedEncodingException encodingException = null;
			try {
				byte[] asciiBytes = key.getBytes("ASCII");
				byte[] utf8Bytes = key.getBytes("UTF-8");
				validAscii = Arrays.equals(asciiBytes, utf8Bytes);
			} catch (UnsupportedEncodingException e) {
				// Shouldn't ever happen
				encodingException = e;
			}
			if (!validAscii) {
				String message = "User metadata name is incompatible with the CS REST interface, "
						+ "only ASCII characters are allowed in HTTP headers: "
						+ key;
				if (encodingException == null) {
					log.error(message);
					throw new ServiceException(message);
				} else {
					log.error(message, encodingException);
					throw new ServiceException(message, encodingException);
				}
			}

			// Fail early if user-supplied metadata cannot be represented as
			// valid HTTP headers,
			// rather than waiting for a SignatureDoesNotMatch error.
			// NOTE: These checks are very much incomplete.
			if (value.indexOf('\n') >= 0 || value.indexOf('\r') >= 0) {
				String errorMessage = "The value of metadata item "
						+ key
						+ " cannot be represented as an HTTP header for the REST CloudStorage interface: "
						+ value;
				log.error(errorMessage);
				throw new ServiceException(errorMessage);
			}

			// Ensure each SNDA header is uniquely identified according to the
			// lowercase name.
			String duplicateValue = (String) headersAlreadySeenMap.get(key
					.toLowerCase());
			if (duplicateValue != null && !duplicateValue.equals(value)) {
				String errorMessage = "HTTP header name occurs multiple times in request with different values, "
						+ "probably due to mismatched capitalization when setting metadata names. "
						+ "Duplicate metadata name: '"
						+ key
						+ "', All metadata: " + metadata;
				log.error(errorMessage);
				throw new ServiceException(errorMessage);
			}

			httpMethod.addHeader(key, value);
			headersAlreadySeenMap.put(key.toLowerCase(), value);
		}
	}
	
	@Override
	protected StorageBucket[] listAllBucketsImpl() {
		String bucketName = "";
		HttpResponse response = performRestGet(bucketName, null, null, null);
		String contentType = response.getHeaders("Content-Type")[0].getValue();
		
		if (!isXmlContentType(contentType)) {
			String errorMessage = "Expected XML document response from Cloud Storage but received content type "
					+ contentType;
			log.error(errorMessage);
            throw new ServiceException(errorMessage);
        }
		
		try {
			return getXmlResponseSaxParser()
									.parseListMyBucketsResponse(response.getEntity().getContent())
									.getBuckets();
		} catch (IOException e) {
			log.error("Occurred an IOException while listing all buckets.", e);
			throw new ServiceException(e);
		}
	}
	
	@Override
	protected StorageObject[] listObjectsImpl(String bucketName, String prefix,
			String delimiter, long maxListingLength) {
		return listObjectsInternal(bucketName, prefix, delimiter,
				maxListingLength, true, null, null).getObjects();
	}
	
	@Override
	protected StorageObject[] listObjectsImpl(String bucketName, String prefix,
            String delimiter, String marker, long maxListingLength) {
		return listObjectsInternal(bucketName, prefix, delimiter,
				maxListingLength, true, marker, null).getObjects();
	}
	
	@Override
    protected StorageObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix, String delimiter,
        long maxListingLength, String priorLastKey, boolean completeListing) {
        return listObjectsInternal(bucketName, prefix, delimiter,
            maxListingLength, completeListing, priorLastKey, null);
    }
	
	protected StorageObjectsChunk listObjectsInternal(String bucketName,
			String prefix, String delimiter, long maxListingLength,
			boolean automaticallyMergeChunks, String priorLastKey,
			String priorLastVersion) {
		Map<String, String> parameters = new HashMap<String, String>();
		if (prefix != null) {
			parameters.put("prefix", prefix);
		}
		if (delimiter != null) {
			parameters.put("delimiter", delimiter);
		}
		if (maxListingLength > 0) {
			parameters.put("max-keys", String.valueOf(maxListingLength));
		}

		List<StorageObject> objects = new ArrayList<StorageObject>();
		List<String> commonPrefixes = new ArrayList<String>();

		boolean incompleteListing = true;
		int ioErrorRetryCount = 0;

		while (incompleteListing) {
			if (priorLastKey != null) {
				parameters.put("marker", priorLastKey);
			} else {
				parameters.remove("marker");
			}

			HttpResponse response = performRestGet(bucketName, null,
					parameters, null);
			ListBucketHandler listBucketHandler = null;

			try {
				listBucketHandler = getXmlResponseSaxParser()
						.parseListBucketResponse(response.getEntity().getContent());
				ioErrorRetryCount = 0;
			} catch (IOException e) {
				if (ioErrorRetryCount < 5) {
					ioErrorRetryCount++;
					if (log.isWarnEnabled()) {
						log.warn(
								"Retrying bucket listing failure due to IO error",
								e);
					}
					continue;
				} else {
					throw new ServiceException(e);
				}
			}

			StorageObject[] partialObjects = listBucketHandler.getObjects();
			if (log.isDebugEnabled()) {
				log.debug("Found " + partialObjects.length
						+ " objects in one batch");
			}
			objects.addAll(Arrays.asList(partialObjects));

			String[] partialCommonPrefixes = listBucketHandler
					.getCommonPrefixes();
			if (log.isDebugEnabled()) {
				log.debug("Found " + partialCommonPrefixes.length
						+ " common prefixes in one batch");
			}
			commonPrefixes.addAll(Arrays.asList(partialCommonPrefixes));

			incompleteListing = listBucketHandler.isListingTruncated();
			if (incompleteListing) {
				priorLastKey = listBucketHandler.getMarkerForNextListing();
				if (log.isDebugEnabled()) {
					log.debug("Yet to receive complete listing of bucket contents, "
							+ "last key for prior chunk: " + priorLastKey);
				}
			} else {
				priorLastKey = null;
			}

			if (!automaticallyMergeChunks) {
				break;
			}
		}
		if (automaticallyMergeChunks) {
			if (log.isDebugEnabled()) {
				log.debug("Found " + objects.size() + " objects in total");
			}
			return new StorageObjectsChunk(prefix, delimiter,
					objects.toArray(new StorageObject[objects.size()]),
					commonPrefixes.toArray(new String[commonPrefixes.size()]),
					null);
		} else {
			return new StorageObjectsChunk(prefix, delimiter,
					objects.toArray(new StorageObject[objects.size()]),
					commonPrefixes.toArray(new String[commonPrefixes.size()]),
					priorLastKey);
		}
	}
	
	@Override
	protected StorageBucket createBucketImpl(String bucketName, String location) {
		Map<String, Object> metadata = new HashMap<String, Object>();
		HttpEntity requestEntity = null;

//		if (location != null /*&& !Constants.CS_DEFAULT_LOCATION.equalsIgnoreCase(location)*/) {
//            
//        }
		if (location == null) {
			location = Constants.CS_DEFAULT_LOCATION;
		}
		
		metadata.put("Content-Type", "text/xml");
        try {
            CreateBucketConfiguration config = new CreateBucketConfiguration(location);
            String configXml = config.toXml();
            metadata.put("Content-Length", String.valueOf(configXml.length()));
            System.out.println(configXml);
            requestEntity = new StringEntity(configXml, "text/xml", Constants.DEFAULT_ENCODING);
        } catch (Exception e) {
            throw new ServiceException("Unable to encode CreateBucketConfiguration XML document", e);
        }

		Map<String, Object> map = createObjectImpl(bucketName, null, null,
				requestEntity, metadata, null, null);
		
		StorageBucket bucket = newBucket();
		bucket.setName(bucketName);
		if (bucket instanceof CSBucket) {
			((CSBucket) bucket).setLocation(location);
		}
		bucket.replaceAllMetadata(map);
		return bucket;
	}
	
	@Override
	protected void deleteBucketImpl(String bucketName) {
		performRestDelete(bucketName, null, null);
	}
	
	protected boolean isLiveMD5HashingRequired(StorageObject object){
        // We do not need to calculate the data MD5 hash during upload if the
        // expected hash value was provided as the object's Content-MD5 header.
        if (object.getMetadata(StorageObject.METADATA_HEADER_CONTENT_MD5) != null) {
            return false;
        }
        return true;
    }
	
	@Override
	protected StorageObject putObjectImpl(String bucketName, StorageObject object) {
		
		if (log.isDebugEnabled()) {
            log.debug("Creating Object with key " + object.getKey() + " in bucket " + bucketName);
        }
		
		// We do not need to calculate the data MD5 hash during upload if the
        // expected hash value was provided as the object's Content-MD5 header.
        boolean isLiveMD5HashingRequired =
            (object.getMetadata(StorageObject.METADATA_HEADER_CONTENT_MD5) == null);
        
		HttpEntity requestEntity = new InputStreamEntity(object.getDataInputStream(), object.getContentLength());
		this.putObjectWithRequestEntityImpl(bucketName, object, requestEntity, null);
		return object;
	}
	
	protected void putObjectWithRequestEntityImpl(String bucketName,
			StorageObject object, HttpEntity requestEntity,
			Map<String, String> requestParams) {
		Map<String, Object> map = createObjectImpl(bucketName, object.getKey(), object.getContentType(),
				requestEntity, object.getMetadataMap(), requestParams, object.getStorageClass());
		try {
            object.closeDataInputStream();
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to close data input stream for object '" + object.getKey() + "'", e);
            }
        }

        // Populate object with result metadata.
        object.replaceAllMetadata(map);
	}
	
	protected Map<String, Object> createObjectImpl(String bucketName, String objectKey, String contentType,
	        HttpEntity requestEntity, Map<String, Object> metadata,
	        Map<String, String> requestParams, String storageClass) {
		
		if (metadata == null) {
            metadata = new HashMap<String, Object>();
        } else {
            // Use a new map object in case the one we were provided is immutable.
            metadata = new HashMap<String, Object>(metadata);
        }
        if (contentType != null) {
            metadata.put("Content-Type", contentType);
        } else {
            metadata.put("Content-Type", Mimetypes.MIMETYPE_OCTET_STREAM);
        }
        
        prepareStorageClass(metadata, storageClass, true, objectKey);
        
        HttpResponse response = performRestPut(bucketName, objectKey, metadata, requestParams, requestEntity, true);
		Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(metadata); // Keep existing metadata.
        map.putAll(convertHeadersToMap(response.getAllHeaders()));
        if (requestEntity != null) {
        	map.put(StorageObject.METADATA_HEADER_CONTENT_LENGTH, String.valueOf(requestEntity.getContentLength()));
        }
        map = ServiceUtils.cleanRestMetadataMap(
            map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix());
        
		return map;
	}
	
	protected void prepareStorageClass(Map<String, Object> metadata, String storageClass,
            boolean useDefaultStorageClass, String objectKey) {
        if (metadata == null) {
            throw new IllegalArgumentException("Null metadata not allowed.");
        }
        if (getEnableStorageClasses()) {
            if (storageClass == null
                && useDefaultStorageClass
                && this.defaultStorageClass != null) {
                // Apply default storage class
                storageClass = this.defaultStorageClass;
                log.debug("Applied default storage class '" + storageClass
                    + "' to object '" + objectKey + "'");
            }
            if (storageClass != null && storageClass != "") {
                metadata.put(this.getRestHeaderPrefix() + "storage-class", storageClass);
            }
        }
    }
	
	@Override
	protected void deleteObjectImpl(String bucketName, String objectKey) {
		performRestDelete(bucketName, objectKey, null);
	}
	
	@Override
	protected StorageObject headObjectImpl(String bucketName, String objectKey) {
		HttpResponse response = performRestHead(bucketName, objectKey, null, null);

		if (response.getStatusLine().getStatusCode() != 200) {
			return null;
		}
		
		Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(convertHeadersToMap(response.getAllHeaders()));
		
		StorageObject responseObject = newObject();
        responseObject.setKey(objectKey);
        responseObject.setBucketName(bucketName);
        responseObject.replaceAllMetadata(ServiceUtils.cleanRestMetadataMap(
            map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix()));
        responseObject.setMetadataComplete(true); // Flag this object as having the complete metadata set.
		
		return responseObject;
	}
	
	@Override
	protected StorageObject getObjectImpl(String bucketName, String objectKey, 
	        String[] ifMatchTags, Long byteRangeStart, Long byteRangeEnd) {
		Map<String, Object> requestHeaders = new HashMap<String, Object>();
        Map<String, String> requestParameters = new HashMap<String, String>();
		
		if (byteRangeStart != null || byteRangeEnd != null) {
            String range = "bytes="
                + (byteRangeStart != null? byteRangeStart.toString() : "")
                + "-"
                + (byteRangeEnd != null? byteRangeEnd.toString() : "");
            requestHeaders.put("Range", range);
        }
		
		HttpResponse response = performRestGet(bucketName, objectKey, requestParameters, requestHeaders);
        
        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(convertHeadersToMap(response.getAllHeaders()));

        StorageObject responseObject = newObject();
        responseObject.setKey(objectKey);
        responseObject.setBucketName(bucketName);
        responseObject.replaceAllMetadata(ServiceUtils.cleanRestMetadataMap(
            map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix()));
        responseObject.setMetadataComplete(true); // Flag this object as having the complete metadata set.
        try {
			responseObject.setDataInputStream(response.getEntity().getContent());
		} catch (IOException e) {
			log.error("Occurred an IOException while get object.", e);
			throw new ServiceException(e);
		}

        return responseObject;
	}
	
	@Override
	protected Map<String, Object> copyObjectImpl(String sourceBucketName, String sourceObjectKey,
	        String destinationBucketName, String destinationObjectKey,
	        Map<String, Object> destinationMetadata, Calendar ifModifiedSince,
	        Calendar ifUnmodifiedSince, String[] ifMatchTags, String[] ifNoneMatchTags,
	        String destinationObjectStorageClass) {
		Map<String, Object> metadata = new HashMap<String, Object>();

        String sourceKey = RestUtils.encodeUrlString(sourceBucketName + Constants.VIRGULE + sourceObjectKey);
        metadata.put(this.getRestHeaderPrefix() + "copy-source", sourceKey);
        
        prepareStorageClass(metadata, destinationObjectStorageClass, false, destinationObjectKey);
        
        if (destinationMetadata != null) {
            metadata.put(this.getRestHeaderPrefix() + "metadata-directive", "REPLACE");
            // Include any metadata provided with CS object.
            metadata.putAll(destinationMetadata);
            // Set default content type.
            if (!metadata.containsKey("Content-Type")) {
                metadata.put("Content-Type", Mimetypes.MIMETYPE_OCTET_STREAM);
            }
        } else {
            metadata.put(this.getRestHeaderPrefix() + "metadata-directive", "COPY");
        }
        
        if (ifModifiedSince != null) {
            metadata.put(this.getRestHeaderPrefix() + "copy-source-if-modified-since",
                ServiceUtils.formatRfc822Date(ifModifiedSince.getTime()));
            if (log.isDebugEnabled()) {
                log.debug("Only copy object if-modified-since:" + ifModifiedSince);
            }
        }
        if (ifUnmodifiedSince != null) {
            metadata.put(this.getRestHeaderPrefix() + "copy-source-if-unmodified-since",
                ServiceUtils.formatRfc822Date(ifUnmodifiedSince.getTime()));
            if (log.isDebugEnabled()) {
                log.debug("Only copy object if-unmodified-since:" + ifUnmodifiedSince);
            }
        }
        if (ifMatchTags != null) {
            String tags = ServiceUtils.join(ifMatchTags, ",");
            metadata.put(this.getRestHeaderPrefix() + "copy-source-if-match", tags);
            if (log.isDebugEnabled()) {
                log.debug("Only copy object based on hash comparison if-match:" + tags);
            }
        }
        if (ifNoneMatchTags != null) {
            String tags = ServiceUtils.join(ifNoneMatchTags, ",");
            metadata.put(this.getRestHeaderPrefix() + "copy-source-if-none-match", tags);
            if (log.isDebugEnabled()) {
                log.debug("Only copy object based on hash comparison if-none-match:" + tags);
            }
        }
        
        HttpResponse response = performRestPut(destinationBucketName,
				destinationObjectKey, metadata, null, null, false);

        String content = null;
        try {
			content = ServiceUtils.readInputStreamToString(response.getEntity().getContent(), "UTF-8").trim();
		} catch (IOException e) {
			log.error("Get response content error.", e);
			throw new RuntimeException(e);
		}
        CopyObjectResultHandler handler = getXmlResponseSaxParser()
            .parseCopyObjectResponse(
                new ByteArrayInputStream(content.getBytes()));

        // Release HTTP connection manually. This should already have been done by the
        // HttpMethodReleaseInputStream class, but you can never be too sure...
        releaseConnection(response);

        if (handler.isErrorResponse()) {
            throw new ServiceException(
                "Copy failed: Code=" + handler.getErrorCode() +
                ", Message=" + handler.getErrorMessage() +
                ", RequestId=" + handler.getErrorRequestId() +
                ", HostId=" + handler.getErrorHostId());
        }

        Map<String, Object> map = new HashMap<String, Object>();

        // Result fields returned when copy is successful.
        map.put("Last-Modified", handler.getLastModified());
        map.put("ETag", handler.getETag());

        // Include response headers in result map.
        map.putAll(convertHeadersToMap(response.getAllHeaders()));
        map = ServiceUtils.cleanRestMetadataMap(
            map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix());
        
        return map;
	}
	
	/**
     * Puts an object using a pre-signed PUT URL generated for that object.
     * <p>
     * This operation does not required any CS functionality as it merely
     * uploads the object by performing a standard HTTP PUT using the signed URL.
     *
     * @param signedPutUrl
     * a signed PUT URL generated with
     * {@link com.snda.storage.service.CSService#createSignedPutUrl(String, String, java.util.Map, java.util.Date)}.
     * @param object
     * the object to upload, which must correspond to the object for which the URL was signed.
     *
     * @return
     * the CSObject put to CS. The CSObject returned will represent the object created in CS.
     *
     */
	public CSObject putObjectWithSignedUrl(String signedPutUrl, CSObject object) {
		HttpPut putMethod = new HttpPut(signedPutUrl);

        Map<String, Object> renamedMetadata = renameMetadataKeys(object.getMetadataMap());
        addMetadataToHeaders(putMethod, renamedMetadata);

        if (!object.containsMetadata("Content-Length")) {
            throw new IllegalStateException("Content-Length must be specified for objects put using signed PUT URLs");
        }

        HttpEntity requestEntity = null;

        // We do not need to calculate the data MD5 hash during upload if the
        // expected hash value was provided as the object's Content-MD5 header.
        boolean isLiveMD5HashingRequired = isLiveMD5HashingRequired(object);

        String csEndpoint = this.getEndpoint();

        if (object.getDataInputStream() != null) {
            requestEntity = new InputStreamEntity(object.getDataInputStream(), object.getContentLength());
            putMethod.setEntity(requestEntity);
        }

        HttpResponse httpResponse = performRequest(putMethod, new int[] {200, 204});

        // Consume response data and release connection.
        releaseConnection(httpResponse);
        try {
            object.closeDataInputStream();
        } catch (IOException e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to close data input stream for object '" + object.getKey() + "'", e);
            }
        }

        try {
            StorageObject uploadedObject = ServiceUtils.buildObjectFromUrl(
                putMethod.getURI().getHost(),
                putMethod.getURI().getRawPath(),
                csEndpoint);
            uploadedObject.setBucketName(uploadedObject.getBucketName());

            // Add all metadata returned by CS to uploaded object.
            Map<String, Object> map = new HashMap<String, Object>();
            map.putAll(convertHeadersToMap(httpResponse.getAllHeaders()));
            uploadedObject.replaceAllMetadata(ServiceUtils.cleanRestMetadataMap(
                map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix()));

            return (CSObject) uploadedObject;
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to determine name of object created with signed PUT", e);
        }
	}
	
	/**
     * Deletes an object using a pre-signed DELETE URL generated for that object.
     * <p>
     * This operation does not required any CS functionality as it merely
     * deletes the object by performing a standard HTTP DELETE using the signed URL.
     *
     * @param signedDeleteUrl
     * a signed DELETE URL generated with {@link com.snda.storage.service.CSService#createSignedDeleteUrl}.
     *
     */
	public void deleteObjectWithSignedUrl(String signedDeleteUrl) {
		HttpDelete deleteMethod = new HttpDelete(signedDeleteUrl);
        HttpResponse response = performRequest(deleteMethod, new int[] {204, 200});
        releaseConnection(response);
	}
	
	/**
     * Gets an object using a pre-signed GET URL generated for that object.
     * <p>
     * This operation does not required any CS functionality as it merely
     * uploads the object by performing a standard HTTP GET using the signed URL.
     *
     * @param signedGetUrl
     * a signed GET URL generated with
     * {@link com.snda.storage.service.CSService#createSignedGetUrl(String, String, java.util.Date)}.
     *
     * @return
     * the CSObject in CS including all metadata and the object's data input stream.
     *
     */
	public CSObject getObjectWithSignedUrl(String signedGetUrl) {
		return getObjectWithSignedUrlImpl(signedGetUrl, false);
	}
	
	/**
     * Gets an object's details using a pre-signed HEAD URL generated for that object.
     * <p>
     * This operation does not required any CS functionality as it merely
     * uploads the object by performing a standard HTTP HEAD using the signed URL.
     *
     * @param signedHeadUrl
     * a signed HEAD URL generated with
     * {@link com.snda.storage.service.CSService#createSignedHeadUrl(String, String, java.util.Date)}.
     *
     * @return
     * the CSObject in CS including all metadata, but without the object's data input stream.
     *
     */
	public CSObject getObjectDetailsWithSignedUrl(String signedHeadUrl) {
		return getObjectWithSignedUrlImpl(signedHeadUrl, true);
	}
	
	/**
     * @param contentType
     * @return true if the given Content-Type string represents an XML document.
     */
	protected boolean isXmlContentType(String contentType) {
		if (contentType != null
				&& (contentType.toLowerCase().startsWith(Mimetypes.MIMETYPE_TXT_XML.toLowerCase()) 
						|| contentType.toLowerCase().startsWith(Mimetypes.MIMETYPE_APPLICATION_XML.toLowerCase()))) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
     * Authorizes an HTTP request by signing it. The signature is based on the target URL, and the
     * signed authorization string is added to the {@link org.apache.http.client.methods.HttpRequestBase} object as an Authorization header.
     *
     * @param httpMethod
     *        the request object
     */
	public void authorizeHttpRequest(HttpRequestBase httpMethod) {
//        String hostname = httpMethod.getURI().getHost();

        String fullUrl = httpMethod.getURI().getRawPath();
        
        String queryString = httpMethod.getURI().getQuery();
        if (queryString != null && queryString.length() > 0) {
            fullUrl += "?" + queryString;
        }

        httpMethod.addHeader("Date", ServiceUtils.formatRfc822Date(
            getCurrentTime()));

        // Generate a canonical string representing the operation.
        String canonicalString = null;
		try {
			canonicalString = RestUtils.makeServiceCanonicalString(
			        httpMethod.getMethod(), fullUrl,
			        convertHeadersToMap(httpMethod.getAllHeaders()), null,
			        this.getRestHeaderPrefix(), this.getResourceParameterNames());
		} catch (UnsupportedEncodingException e) {
			log.error("Occurred an UnsupportedEncodingException while authorizing request.", e);
			throw new ServiceException(e);
		}

        // Sign the canonical string.
        String signedCanonical = ServiceUtils.signWithHmacSha1(
            getProviderCredentials().getSecretKey(), canonicalString);

        // Add encoded authorization to connection as HTTP Authorization header.
        String authorizationString = getSignatureIdentifier() + " "
            + getProviderCredentials().getAccessKey() + ":" + signedCanonical;
        httpMethod.addHeader("Authorization", authorizationString);
    }
	
	/**
     * Adds all the provided request parameters to a URL in GET request format.
     *
     * @param urlPath
     *        the target URL
     * @param requestParameters
     *        the parameters to add to the URL as GET request params.
     * @return
     * the target URL including the parameters.
     */
    protected String addRequestParametersToUrlPath(String urlPath,
        Map<String, String> requestParameters) throws ServiceException
    {
        if (requestParameters != null) {
            for (Map.Entry<String, String> entry: requestParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                urlPath += (urlPath.indexOf("?") < 0 ? "?" : "&")
                    + RestUtils.encodeUrlString(key);
                if (value != null && value.length() > 0) {
                    urlPath += "=" + RestUtils.encodeUrlString(value);
                    if (log.isDebugEnabled()) {
                        log.debug("Added request parameter: " + key + "=" + value);
                    }
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug("Added request parameter without value: " + key);
                    }
                }
            }
        }
        return urlPath;
    }
	
	/**
     * Shut down all connections managed by the underlying HttpConnectionManager.
     */
	@Override
    protected void shutdownImpl() {
		this.httpClient.getConnectionManager().shutdown();
	}
	
	protected HttpContext createHttpContext(AbstractHttpClient absHttpClient) {
        HttpContext context = new BasicHttpContext();
        context.setAttribute(
                ClientContext.SCHEME_REGISTRY,
                absHttpClient.getConnectionManager().getSchemeRegistry());
        context.setAttribute(
                ClientContext.AUTHSCHEME_REGISTRY,
                absHttpClient.getAuthSchemes());
        context.setAttribute(
                ClientContext.COOKIESPEC_REGISTRY,
                absHttpClient.getCookieSpecs());
        context.setAttribute(
                ClientContext.COOKIE_STORE,
                absHttpClient.getCookieStore());
        context.setAttribute(
                ClientContext.CREDS_PROVIDER,
                absHttpClient.getCredentialsProvider());
        return context;
    }
	
	private CSObject getObjectWithSignedUrlImpl(String signedGetOrHeadUrl, boolean headOnly) {
		String csEndpoint = this.getSignedUrlEndpoint();

        HttpRequestBase httpMethod = null;
        if (headOnly) {
            httpMethod = new HttpHead(signedGetOrHeadUrl);
        } else {
            httpMethod = new HttpGet(signedGetOrHeadUrl);
        }

        HttpResponse httpResponse = performRequest(httpMethod, new int[] {200});

        Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(convertHeadersToMap(httpResponse.getAllHeaders()));

        CSObject responseObject = null;
        try {
            responseObject = ServiceUtils.buildObjectFromUrl(
                httpMethod.getURI().getHost(),
                httpMethod.getURI().getRawPath().substring(1),
                csEndpoint);
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException("Unable to determine name of object created with signed PUT", e);
        }

        responseObject.replaceAllMetadata(ServiceUtils.cleanRestMetadataMap(
            map, this.getRestHeaderPrefix(), this.getRestMetadataPrefix()));
        responseObject.setMetadataComplete(true); // Flag this object as having the complete metadata set.
        if (!headOnly) {
            HttpMethodReleaseInputStream releaseIS = new HttpMethodReleaseInputStream(httpResponse);
            responseObject.setDataInputStream(releaseIS);
        } else {
            // Release connection after HEAD (there's no response content)
            if (log.isDebugEnabled()) {
                log.debug("Releasing HttpMethod after HEAD");
            }
            releaseConnection(httpResponse);
        }

        return responseObject;
	}
}
