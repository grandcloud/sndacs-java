package com.snda.storage.service.impl.rest.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jamesmurty.utils.XMLBuilder;
import com.snda.storage.exceptions.CSServiceException;
import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.service.CSService;
import com.snda.storage.service.Constants;
import com.snda.storage.service.MultipartUploadChunk;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartCompleted;
import com.snda.storage.service.model.MultipartPart;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.utils.RestUtils;
import com.snda.storage.service.utils.XmlResponsesSaxParser.CompleteMultipartUploadResultHandler;
import com.snda.storage.service.utils.XmlResponsesSaxParser.ListMultipartPartsResultHandler;
import com.snda.storage.service.utils.XmlResponsesSaxParser.ListMultipartUploadsResultHandler;

/**
 * REST/HTTP implementation of an CSService based on the
 * <a href="http://jakarta.apache.org/commons/httpclient/">HttpClient</a> library.
 * 
 * @author snda
 *
 */
public class RestCSService extends CSService {

	private static final Logger log = LoggerFactory.getLogger(RestCSService.class);
	
	private static final String SNDA_SIGNATURE_IDENTIFIER = "SNDA";
    private static final String SNDA_REST_HEADER_PREFIX = "x-snda-";
    private static final String SNDA_REST_METADATA_PREFIX = "x-snda-meta-";
    
    /**
     * Constructs the service and initialises the properties.
     *
     * @param credentials
     * the user credentials to use when communicating with Cloud Storage.
     *
     */
    public RestCSService(ProviderCredentials credentials) {
    	super(credentials);
    }
    
    /**
     * @return
     * header prefix for general SNDA headers: x-snda-.
     */
	@Override
	public String getRestHeaderPrefix() {
		return SNDA_REST_HEADER_PREFIX;
	}

	/**
     * @return
     * header prefix for SNDA metadata headers: x-snda-meta-.
     */
	@Override
	public String getRestMetadataPrefix() {
		return SNDA_REST_METADATA_PREFIX;
	}
	
	/**
     * @return
     * the identifier for the signature algorithm.
     */
	@Override
	protected String getSignatureIdentifier() {
		return SNDA_SIGNATURE_IDENTIFIER;
	}

	/**
     * @return
     * the port number to be used for insecure connections over HTTP.
     */
	@Override
	protected int getHttpPort() {
		return 80;
	}

	/**
     * @return
     * the port number to be used for secure connections over HTTPS.
     */
	@Override
	protected int getHttpsPort() {
		return 443;
	}
	
	/**
     * @return
     * the endpoint to be used to connect to Cloud Storage.
     */
    @Override
    public String getEndpoint() {
    	return Constants.CS_DEFAULT_HOSTNAME;
    }
    
    /**
     * @return
     * the endpoint to be used to connect to Cloud Storage through signed url
     */
    @Override
	public String getSignedUrlEndpoint() {
		return Constants.CS_SIGNEDURL_HOSTNAME;
	}
    
    @Override
    public List<String> getResourceParameterNames() {
        // Special HTTP parameter names that refer to resources in Cloud Storage
        return Arrays.asList(new String[] {
            "acl", "policy",
            "torrent",
            "logging",
            "location",
            "requestPayment",
            "versions", "versioning", "versionId",
            "uploads", "uploadId", "partNumber",
            "website", "notification"
        });
    }

	@Override
	protected String getBucketLocationImpl(String bucketName) {
		if (log.isDebugEnabled()) {
            log.debug("Retrieving location of Bucket: " + bucketName);
        }

        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("location", "");

        try {
            HttpResponse response = performRestGet(bucketName, null, requestParameters, null);
            return getXmlResponseSaxParser()
                .parseBucketLocationResponse(response.getEntity().getContent());
        } catch (ServiceException e) {
            throw new CSServiceException(e);
        } catch (IOException e) {
        	throw new CSServiceException(e);
		}
	}
	
	@Override
	protected void setBucketPolicyImpl(String bucketName, String policyDocument) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("policy", "");

        Map<String, Object> metadata = new HashMap<String, Object>();
        metadata.put("Content-Type", "text/plain");

        try {
            performRestPut(bucketName, null, metadata, requestParameters,
                new StringEntity(policyDocument, "text/plain", Constants.DEFAULT_ENCODING),
                true);
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        } catch (UnsupportedEncodingException e) {
            throw new CSServiceException("Unable to encode LoggingStatus XML document", e);
        }
	}

	@Override
	protected String getBucketPolicyImpl(String bucketName) {
		try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put("policy", "");

            HttpResponse httpResponse = performRestGet(bucketName, null, requestParameters, null);
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        } catch (IOException e) {
            throw new CSServiceException(e);
        }
	}

	@Override
	protected void deleteBucketPolicyImpl(String bucketName) {
		try {
            Map<String, String> requestParameters = new HashMap<String, String>();
            requestParameters.put("policy", "");
            performRestDelete(bucketName, null, requestParameters);
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	@Override
	protected MultipartUpload multipartStartUploadImpl(String bucketName,
			String objectKey, Map<String, Object> metadataProvided,
			String serverSideEncryptionAlgorithm) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploads", "");

        Map<String, Object> metadata = new HashMap<String, Object>();

        // Use metadata provided, but ignore some items that don't make sense
        if (metadataProvided != null) {
            for (Map.Entry<String, Object> entry: metadataProvided.entrySet()) {
                if (!entry.getKey().toLowerCase().equals("content-length")) {
                    metadata.put(entry.getKey(), entry.getValue());
                }
            }
        }

        // Apply per-object or default object properties when uploading object
//        prepareServerSideEncryption(metadata, serverSideEncryptionAlgorithm, objectKey);

        try {
            HttpResponse httpResponse = performRestPost(
                bucketName, objectKey, metadata, requestParameters, null, false);
            MultipartUpload multipartUpload = getXmlResponseSaxParser()
                .parseInitiateMultipartUploadResult(
                    new HttpMethodReleaseInputStream(httpResponse));
            multipartUpload.setMetadata(metadata); // Add object's known metadata to result object.
            return multipartUpload;
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	@Override
	protected MultipartUploadChunk multipartListUploadsChunkedImpl(
			String bucketName, String prefix, String delimiter,
			String keyMarker, String uploadIdMarker, Integer maxUploads,
			boolean autoMergeChunks) {
		if (bucketName == null || bucketName.length()==0){
            throw new IllegalArgumentException(
                "The bucket name parameter must be specified when listing multipart uploads");
        }
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploads", "");
        if (prefix != null) {
            requestParameters.put("prefix", prefix);
        }
        if (delimiter != null) {
            requestParameters.put("delimiter", delimiter);
        }
        if (maxUploads != null) {
            requestParameters.put("max-uploads", maxUploads.toString());
        }
        if (keyMarker != null) {
            requestParameters.put("key-marker", keyMarker);
        }
        if (uploadIdMarker != null) {
            requestParameters.put("upload-id-marker", uploadIdMarker);
        }

        String nextKeyMarker = keyMarker;
        String nextUploadIdMarker = uploadIdMarker;

        List<MultipartUpload> uploads = new ArrayList<MultipartUpload>();
        List<String> commonPrefixes = new ArrayList<String>();

        boolean incompleteListing = true;
        int ioErrorRetryCount = 0;
        int ioErrorRetryMaxCount = 5;

        try {
            while (incompleteListing) {
                if (nextKeyMarker != null) {
                    requestParameters.put("key-marker", nextKeyMarker);
                } else {
                    requestParameters.remove("key-marker");
                }
                if (nextUploadIdMarker != null) {
                    requestParameters.put("upload-id-marker", nextUploadIdMarker);
                } else {
                    requestParameters.remove("upload-id-marker");
                }

                HttpResponse httpResponse = performRestGet(bucketName, null, requestParameters, null);
                ListMultipartUploadsResultHandler handler = null;
                try {
                    handler = getXmlResponseSaxParser().parseListMultipartUploadsResult(
                        new HttpMethodReleaseInputStream(httpResponse));
                    ioErrorRetryCount = 0;
                } catch (ServiceException e) {
                    if (e.getCause() instanceof IOException
                        && ioErrorRetryCount < ioErrorRetryMaxCount)
                    {
                        ioErrorRetryCount++;
                        if (log.isWarnEnabled()) {
                            log.warn("Retrying bucket listing failure due to IO error", e);
                        }
                        continue;
                    } else {
                        throw e;
                    }
                }

                List<MultipartUpload> partial = handler.getMultipartUploadList();
                if (log.isDebugEnabled()) {
                    log.debug("Found " + partial.size() + " objects in one batch");
                }
                uploads.addAll(partial);

                String[] partialCommonPrefixes = handler.getCommonPrefixes();
                if (log.isDebugEnabled()) {
                    log.debug("Found " + partialCommonPrefixes.length + " common prefixes in one batch");
                }
                commonPrefixes.addAll(Arrays.asList(partialCommonPrefixes));

                incompleteListing = handler.isTruncated();

                if (incompleteListing){
                    nextKeyMarker = handler.getNextKeyMarker();
                    nextUploadIdMarker = handler.getNextUploadIdMarker();
                    // Sanity check for valid pagination values.
                    if (nextKeyMarker == null && nextUploadIdMarker == null) {
                        throw new ServiceException("Unable to retrieve paginated "
                            + "ListMultipartUploadsResult without valid NextKeyMarker "
                            + " or NextUploadIdMarker value.");
                    }
                } else {
                    nextKeyMarker = null;
                    nextUploadIdMarker = null;
                }

                if (!autoMergeChunks){
                    break;
                }
            }

            if (autoMergeChunks && log.isDebugEnabled()) {
                log.debug("Found " + uploads.size() + " uploads in total");
            }

            return new MultipartUploadChunk(
                    prefix,
                    delimiter,
                    uploads.toArray(new MultipartUpload[uploads.size()]),
                    commonPrefixes.toArray(new String[commonPrefixes.size()]),
                    nextKeyMarker,
                    nextUploadIdMarker);
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	@Override
	protected List<MultipartPart> multipartListPartsImpl(String uploadId,
			String bucketName, String objectKey) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploadId", uploadId);
        requestParameters.put("max-parts", "1000");

        try {
            List<MultipartPart> parts = new ArrayList<MultipartPart>();
            String nextPartNumberMarker = null;
            boolean incompleteListing = true;
            do {
                if (nextPartNumberMarker != null) {
                    requestParameters.put("part-number-marker", nextPartNumberMarker);
                } else {
                    requestParameters.remove("part-number-marker");
                }

                HttpResponse httpResponse = performRestGet(bucketName, objectKey, requestParameters, null);
                ListMultipartPartsResultHandler handler = getXmlResponseSaxParser()
                    .parseListMultipartPartsResult(
                        new HttpMethodReleaseInputStream(httpResponse));
                parts.addAll(handler.getMultipartPartList());

                incompleteListing = handler.isTruncated();
                nextPartNumberMarker = handler.getNextPartNumberMarker();

                // Sanity check for valid pagination values.
                if (incompleteListing && nextPartNumberMarker == null)
                {
                    throw new ServiceException("Unable to retrieve paginated "
                        + "ListMultipartPartsResult without valid NextKeyMarker value.");
                }
            } while (incompleteListing);
            return parts;
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	@Override
	protected MultipartCompleted multipartCompleteUploadImpl(String uploadId,
			String bucketName, String objectKey, List<MultipartPart> parts) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploadId", uploadId);

        // Ensure part list is sorted by part number
        MultipartPart[] sortedParts = parts.toArray(new MultipartPart[parts.size()]);
        Arrays.sort(sortedParts, new MultipartPart.PartNumberComparator());
        try {
            XMLBuilder builder = XMLBuilder
                .create("CompleteMultipartUpload")/*.a("xmlns", Constants.XML_NAMESPACE)*/;
            for (MultipartPart part: sortedParts) {
                builder.e("Part")
                    .e("PartNumber").t("" + part.getPartNumber()).up()
                    .e("ETag").t("\"" + part.getEtag() + "\"");
            }

            HttpResponse httpResponse = performRestPostWithXmlBuilder(
                bucketName, objectKey, null, requestParameters, builder);
            CompleteMultipartUploadResultHandler handler = getXmlResponseSaxParser()
                .parseCompleteMultipartUploadResult(
                    new HttpMethodReleaseInputStream(httpResponse));

            // Check whether completion actually succeeded
            if (handler.getServiceException() != null) {
                ServiceException e = handler.getServiceException();
                e.setResponseHeaders(RestUtils.convertHeadersToMap(
                        httpResponse.getAllHeaders()));
                throw e;
            }
            return handler.getMultipartCompleted();
        } catch (CSServiceException se) {
            throw se;
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        } catch (ParserConfigurationException e) {
            throw new CSServiceException(e);
        } catch (FactoryConfigurationError e) {
            throw new CSServiceException(e);
        }
	}

	@Override
	protected MultipartPart multipartUploadPartImpl(String uploadId,
			String bucketName, Integer partNumber, CSObject object) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploadId", uploadId);
        requestParameters.put("partNumber", "" + partNumber);

        // Remove all non-HTTP headers from object metadata for multipart part uploads
        synchronized(object) { // Thread-safe header handling.
            List<String> metadataNamesToRemove = new ArrayList<String>();
            for (String name: object.getMetadataMap().keySet()) {
                if (!RestUtils.HTTP_HEADER_METADATA_NAMES.contains(name.toLowerCase())) {
                    // Actual metadata name in object does not include the prefix
                    metadataNamesToRemove.add(name);
                }
            }
            for (String name: metadataNamesToRemove) {
            	if ("ETag".equalsIgnoreCase(name)
            			|| "Last-Modified".equalsIgnoreCase(name)) {
            		continue;
            	}
                object.removeMetadata(name);
            }
        }

        try {
            // Always disable live MD5 hash check for MultiPart Part uploads, since the ETag
            // hash value returned by S3 is not an MD5 hash of the uploaded data anyway (Issue #141).
            boolean isLiveMD5HashingRequired = false;

            HttpEntity requestEntity = null;
            if (object.getDataInputStream() != null) {
                if (object.containsMetadata(StorageObject.METADATA_HEADER_CONTENT_LENGTH)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Uploading multipart part data with Content-Length: "
                            + object.getContentLength());
                    }
//                    requestEntity = new RepeatableRequestEntity(object.getKey(),
//                        object.getDataInputStream(), object.getContentType(), object.getContentLength(),
//                        this.jets3tProperties, isLiveMD5HashingRequired);
                    requestEntity = new InputStreamEntity(object.getDataInputStream(), object.getContentLength());
                } else {
                    // Use InputStreamRequestEntity for objects with an unknown content length, as the
                    // entity will cache the results and doesn't need to know the data length in advance.
                    if (log.isWarnEnabled()) {
                        log.warn("Content-Length of multipart part stream not set, "
                            + "will automatically determine data length in memory");
                    }
                    requestEntity = new InputStreamEntity(
                        object.getDataInputStream(), -1);
                }
            }

            this.putObjectWithRequestEntityImpl(bucketName, object, requestEntity, requestParameters);

            // Populate part with response data that is accessible via the object's metadata
            MultipartPart part = new MultipartPart(partNumber, object.getLastModifiedDate(),
                object.getETag(), object.getContentLength());
            return part;
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	@Override
	protected void multipartAbortUploadImpl(String uploadId, String bucketName,
			String objectKey) {
		Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put("uploadId", uploadId);

        try {
            performRestDelete(bucketName, objectKey, requestParameters);
        } catch (ServiceException se) {
            throw new CSServiceException(se);
        }
	}

	/**
     * @return
     * If true, it will specify bucket names in the request path of the HTTP message
     * instead of the Host header.
     */
    @Override
    protected boolean getDisableDnsBuckets() {
    	return false;
    }
    
    /**
     * @return
     * If true, it will enable support for Storage Classes.
     */
    @Override
    protected boolean getEnableStorageClasses() {
        return true;
    }

    /**
     * @return
     * the virtual path inside the CS server.
     */
	@Override
	protected String getVirtualPath() {
		return "";
	}

}
