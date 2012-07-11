package com.snda.storage.service.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.service.Constants;
import com.snda.storage.service.impl.rest.SimpleHandler;
import com.snda.storage.service.model.CSBucket;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.CSOwner;
import com.snda.storage.service.model.MultipartCompleted;
import com.snda.storage.service.model.MultipartPart;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageBucket;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.model.StorageOwner;

/**
 * XML Sax parser to read XML documents returned by Cloud Storage via the REST interface, converting these
 * documents into sdk objects.
 * 
 * @author snda
 *
 */
public class XmlResponsesSaxParser {
	private static final Logger log = LoggerFactory.getLogger(XmlResponsesSaxParser.class);
	
	private XMLReader xr = null;
	
	public XmlResponsesSaxParser() {
		this.xr = ServiceUtils.loadXMLReader();
	}
	
	protected StorageBucket newBucket() {
        return new CSBucket();
    }
	
	protected StorageObject newObject() {
        return new CSObject();
    }
	
	protected StorageOwner newOwner() {
		return new CSOwner();
    }
	
	/**
     * Parses an XML document from an input stream using a document handler.
     * @param handler
     *        the handler for the XML document
     * @param inputStream
     *        an input stream containing the XML document to parse
     * @throws ServiceException
     *        any parsing, IO or other exceptions are wrapped in an ServiceException.
     */
	protected void parseXmlInputStream(DefaultHandler handler,
			InputStream inputStream) throws ServiceException {
		try {
			if (log.isDebugEnabled()) {
				log.debug("Parsing XML response document with handler: "
						+ handler.getClass());
			}
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					inputStream, Constants.DEFAULT_ENCODING));
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			xr.parse(new InputSource(breader));
		} catch (Throwable t) {
			try {
				inputStream.close();
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(
							"Unable to close response InputStream up after XML parse failure",
							e);
				}
			}
			throw new ServiceException(
					"Failed to parse XML document with handler "
							+ handler.getClass(), t);
		}
	}
	
	/**
     * Parses a ListBucket response XML document from an input stream.
     * @param inputStream
     * XML data input stream.
     * @return
     * the XML handler object populated with data parsed from the XML stream.
     */
	public ListBucketHandler parseListBucketResponse(InputStream inputStream) {
		ListBucketHandler handler = new ListBucketHandler();
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}
	
	/**
     * Parses a ListAllMyBuckets response XML document from an input stream.
     * @param inputStream
     * XML data input stream.
     * @return
     * the XML handler object populated with data parsed from the XML stream.
     */
	public ListAllMyBucketsHandler parseListMyBucketsResponse(InputStream inputStream) {
		ListAllMyBucketsHandler handler = new ListAllMyBucketsHandler();
        parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
        return handler;
	}
	
	/**
     * Handler for ListBucket response XML documents.
     * The document is parsed into {@link CSObject}s available via the {@link #getObjects()} method.
     */
    public class ListBucketHandler extends DefaultXmlHandler {
        private StorageObject currentObject = null;
        private boolean insideCommonPrefixes = false;

        private final List<StorageObject> objects = new ArrayList<StorageObject>();
        private final List<String> commonPrefixes = new ArrayList<String>();

        // Listing properties.
        private String bucketName = null;
        private String requestPrefix = null;
        private String requestMarker = null;
        private long requestMaxKeys = 0;
        private boolean listingTruncated = false;
        private String lastKey = null;
        private String nextMarker = null;

        /**
         * If the listing is truncated this method will return the marker that should be used
         * in subsequent bucket list calls to complete the listing.
         *
         * @return
         * null if the listing is not truncated, otherwise the next marker if it's available or
         * the last object key seen if the next marker isn't available.
         */
        public String getMarkerForNextListing() {
            if (listingTruncated) {
                if (nextMarker != null) {
                    return nextMarker;
                } else if (lastKey != null) {
                    return lastKey;
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn("Unable to find Next Marker or Last Key for truncated listing");
                    }
                    return null;
                }
            } else {
                return null;
            }
        }

        /**
         * @return
         * true if the listing document was truncated, and therefore only contained a subset of the
         * available CS objects.
         */
        public boolean isListingTruncated() {
            return listingTruncated;
        }

        /**
         * @return
         * the CS objects contained in the listing.
         */
        public StorageObject[] getObjects() {
            return objects.toArray(new StorageObject[objects.size()]);
        }

        public String[] getCommonPrefixes() {
            return commonPrefixes.toArray(new String[commonPrefixes.size()]);
        }

        public String getRequestPrefix() {
            return requestPrefix;
        }

        public String getRequestMarker() {
            return requestMarker;
        }

        public String getNextMarker() {
            return nextMarker;
        }

        public long getRequestMaxKeys() {
            return requestMaxKeys;
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Contents")) {
                currentObject = newObject();
                if (currentObject instanceof CSObject) {
                    ((CSObject)currentObject).setBucketName(bucketName);
                }
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = true;
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            // Listing details
            if (name.equals("Name")) {
                bucketName = elementText;
                if (log.isDebugEnabled()) {
                    log.debug("Examining listing for bucket: " + bucketName);
                }
            } else if (!insideCommonPrefixes && name.equals("Prefix")) {
                requestPrefix = elementText;
            } else if (name.equals("Marker")) {
                requestMarker = elementText;
            } else if (name.equals("NextMarker")) {
                nextMarker = elementText;
            } else if (name.equals("MaxKeys")) {
                requestMaxKeys = Long.parseLong(elementText);
            } else if (name.equals("IsTruncated")) {
                String isTruncatedStr = elementText.toLowerCase(Locale.getDefault());
                if (isTruncatedStr.startsWith("false")) {
                    listingTruncated = false;
                } else if (isTruncatedStr.startsWith("true")) {
                    listingTruncated = true;
                } else {
                    throw new RuntimeException("Invalid value for IsTruncated field: "
                        + isTruncatedStr);
                }
            }
            // Object details.
            else if (name.equals("Contents")) {
                objects.add(currentObject);
                if (log.isDebugEnabled()) {
                    log.debug("Created new object from listing: " + currentObject);
                }
            } else if (name.equals("Key")) {
                currentObject.setKey(elementText);
                lastKey = elementText;
            } else if (name.equals("LastModified")) {
                try {
                    currentObject.setLastModifiedDate(ServiceUtils.parseIso8601Date(elementText));
                } catch (ParseException e) {
                    throw new RuntimeException(
                        "Non-ISO8601 date for LastModified in bucket's object listing output: "
                        + elementText, e);
                }
            } else if (name.equals("ETag")) {
                currentObject.setETag(elementText);
            } else if (name.equals("Size")) {
                currentObject.setContentLength(Long.parseLong(elementText));
            }
            // Common prefixes.
            else if (insideCommonPrefixes && name.equals("Prefix")) {
                commonPrefixes.add(elementText);
            } else if (name.equals("CommonPrefixes")) {
                insideCommonPrefixes = false;
            }
        }
    }
	
	/**
	 * Handler for ListAllMyBuckets response XML documents. The document is parsed into
     * {@link StorageBucket}s available via the {@link #getBuckets()} method.
     * 
	 * @author snda
	 *
	 */
	public class ListAllMyBucketsHandler extends DefaultXmlHandler {
        private StorageBucket currentBucket = null;

        private final List<StorageBucket> buckets = new ArrayList<StorageBucket>();

        /**
         * @return
         * the buckets listed in the document.
         */
        public StorageBucket[] getBuckets() {
            return buckets.toArray(new StorageBucket[buckets.size()]);
        }

        @Override
        public void startElement(String name) {
            if (name.equals("Bucket")) {
                currentBucket = newBucket();
            }
        }

        @Override
        public void endElement(String name, String elementText) {
            // Bucket item details.
            if (name.equals("Bucket")) {
                if (log.isDebugEnabled()) {
                    log.debug("Created new bucket from listing: " + currentBucket);
                }
                buckets.add(currentBucket);
            } else if (name.equals("Name")) {
                currentBucket.setName(elementText);
            } else if (name.equals("CreationDate")) {
            	// use local time
            	// elementText += ".000Z";
                try {
                    currentBucket.setCreationDate(ServiceUtils.parseIso8601Date(elementText));
                } catch (ParseException e) {
                    throw new RuntimeException(
                        "Non-ISO8601 date for CreationDate in list buckets output: "
                        + elementText, e);
                }
            } else if (name.equals("Location")) {
            	((CSBucket)currentBucket).setLocation(elementText);
            }
        }
	}
	
	protected InputStream sanitizeXmlDocument(DefaultHandler handler,
			InputStream inputStream) {

		InputStream sanitizedInputStream = null;

		try {
			/*
			 * Read object listing XML document from input stream provided into
			 * a string buffer, so we can replace troublesome characters before
			 * sending the document to the XML parser.
			 */
			StringBuffer listingDocBuffer = new StringBuffer();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					inputStream, Constants.DEFAULT_ENCODING));

			char[] buf = new char[8192];
			int read = -1;
			while ((read = br.read(buf)) != -1) {
				listingDocBuffer.append(buf, 0, read);
			}
			br.close();

			// Replace any carriage return (\r) characters with explicit XML
			// character entities, to prevent the SAX parser from
			// misinterpreting 0x0D characters as 0x0A.
			String listingDoc = listingDocBuffer.toString().replaceAll("\r",
					"&#013;");

			sanitizedInputStream = new ByteArrayInputStream(
					listingDoc.getBytes(Constants.DEFAULT_ENCODING));
		} catch (Throwable t) {
			try {
				inputStream.close();
			} catch (IOException e) {
				if (log.isErrorEnabled()) {
					log.error(
							"Unable to close response InputStream after failure sanitizing XML document",
							e);
				}
			}
			throw new ServiceException(
					"Failed to sanitize XML document destined for handler "
							+ handler.getClass(), t);
		}
		return sanitizedInputStream;
	}

	public String parseBucketLocationResponse(InputStream inputStream) {
		BucketLocationHandler handler = new BucketLocationHandler();
        parseXmlInputStream(handler, inputStream);
        return handler.getLocation();
	}
	
	public MultipartUpload parseInitiateMultipartUploadResult(InputStream inputStream) {
		MultipartUploadResultHandler handler = new MultipartUploadResultHandler(xr);
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler.getMultipartUpload();
	}
	
	public ListMultipartUploadsResultHandler parseListMultipartUploadsResult(
	        InputStream inputStream) {
		ListMultipartUploadsResultHandler handler = new ListMultipartUploadsResultHandler(xr);
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}
	
	public ListMultipartPartsResultHandler parseListMultipartPartsResult(
			InputStream inputStream) {
		ListMultipartPartsResultHandler handler = new ListMultipartPartsResultHandler(xr);
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}
	
	public CompleteMultipartUploadResultHandler parseCompleteMultipartUploadResult(
			InputStream inputStream) {
		CompleteMultipartUploadResultHandler handler = new CompleteMultipartUploadResultHandler(xr);
		parseXmlInputStream(handler, sanitizeXmlDocument(handler, inputStream));
		return handler;
	}
	
	/**
     * Handler for CreateBucketConfiguration response XML documents for a bucket.
     * The document is parsed into a String representing the bucket's location,
     * available via the {@link #getLocation()} method.
     *
     */
    public static class BucketLocationHandler extends DefaultXmlHandler {
        private String location = null;

        /**
         * @return
         * the bucket's location.
         */
        public String getLocation() {
            return location;
        }

        @Override
        public void endElement(String name, String elementText) {
            if (name.equals("LocationConstraint")) {
                if (elementText.length() == 0) {
                    location = "huadong-1";
                } else {
                    location = elementText;
                }
            }
        }
    }
    
    public class OwnerHandler extends SimpleHandler {
        private String id;
        private String displayName;

        public OwnerHandler(XMLReader xr) {
            super(xr);
        }

        public StorageOwner getOwner() {
            StorageOwner owner = newOwner();
            owner.setId(id);
            owner.setDisplayName(displayName);
            return owner;
        }

        public void endID(String text) {
            this.id = text;
        }

        public void endDisplayName(String text) {
            this.displayName = text;
        }

        public void endOwner(String text) {
            returnControlToParentHandler();
        }

        // </Initiator> represents end of an owner item in ListMultipartUploadsResult/Upload
        public void endInitiator(String text) {
            returnControlToParentHandler();
        }
    }
    
    public class MultipartUploadResultHandler extends SimpleHandler {
        private String uploadId;
        private String bucketName;
        private String objectKey;
        private String storageClass;
        private CSOwner owner;
        private CSOwner initiator;
        private Date initiatedDate;

        private boolean inInitiator = false;

        public MultipartUploadResultHandler(XMLReader xr) {
            super(xr);
        }

        public MultipartUpload getMultipartUpload() {
            if (initiatedDate != null) {
                // Return the contents from a ListMultipartUploadsResult response
                return new MultipartUpload(uploadId, objectKey, storageClass,
                    initiator, owner, initiatedDate);
            } else {
                // Return the contents from an InitiateMultipartUploadsResult response
                return new MultipartUpload(uploadId, bucketName, objectKey);
            }
        }

        public void endUploadId(String text) {
            this.uploadId = text;
        }

        public void endBucket(String text) {
            this.bucketName = text;
        }

        public void endKey(String text) {
            this.objectKey = text;
        }

        public void endStorageClass(String text) {
            this.storageClass = text;
        }

        public void endInitiated(String text) throws ParseException {
            this.initiatedDate = ServiceUtils.parseIso8601Date(text);
        }

        public void startOwner() {
            inInitiator = false;
            transferControlToHandler(new OwnerHandler(xr));
        }

        public void startInitiator() {
            inInitiator = true;
            transferControlToHandler(new OwnerHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (inInitiator) {
                this.owner = (CSOwner) ((OwnerHandler) childHandler).getOwner();
            } else {
                this.initiator = (CSOwner) ((OwnerHandler) childHandler).getOwner();
            }
        }

        // </Upload> represents end of a MultipartUpload item in ListMultipartUploadsResult
        public void endUpload(String text) {
            returnControlToParentHandler();
        }
    }
    
    public class ListMultipartUploadsResultHandler extends SimpleHandler {
        private final List<MultipartUpload> uploads = new ArrayList<MultipartUpload>();
        private final List<String> commonPrefixes = new ArrayList<String>();
        private boolean insideCommonPrefixes;
        private String bucketName = null;
        private String keyMarker = null;
        private String uploadIdMarker = null;
        private String nextKeyMarker = null;
        private String nextUploadIdMarker = null;
        private int maxUploads = 1000;
        private boolean isTruncated = false;

        public ListMultipartUploadsResultHandler(XMLReader xr) {
            super(xr);
        }

        public List<MultipartUpload> getMultipartUploadList() {
            // Update multipart upload objects with overall bucket name
            for (MultipartUpload upload: uploads) {
                upload.setBucketName(bucketName);
            }
            return uploads;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getKeyMarker() {
            return keyMarker;
        }

        public String getUploadIdMarker() {
            return uploadIdMarker;
        }

        public String getNextKeyMarker() {
            return nextKeyMarker;
        }

        public String getNextUploadIdMarker() {
            return nextUploadIdMarker;
        }

        public int getMaxUploads() {
            return maxUploads;
        }

        public String[] getCommonPrefixes() {
            return commonPrefixes.toArray(new String[commonPrefixes.size()]);
        }

        public void startUpload() {
            transferControlToHandler(new MultipartUploadResultHandler(xr));
        }

        public void startCommonPrefixes(){
            insideCommonPrefixes = true;
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            uploads.add(
                ((MultipartUploadResultHandler) childHandler).getMultipartUpload());
        }

        public void endBucket(String text) {
            this.bucketName = text;
        }

        public void endKeyMarker(String text) {
            this.keyMarker = text;
        }

        public void endUploadIdMarker(String text) {
            this.uploadIdMarker = text;
        }

        public void endNextKeyMarker(String text) {
            this.nextKeyMarker = text;
        }

        public void endNextUploadIdMarker(String text) {
            this.nextUploadIdMarker = text;
        }

        public void endMaxUploads(String text) {
            this.maxUploads = Integer.parseInt(text);
        }

        public void endIsTruncated(String text) {
            this.isTruncated = "true".equalsIgnoreCase(text);
        }

        public void endPrefix(String text) {
            if (insideCommonPrefixes){
                commonPrefixes.add(text);
            }
        }

        public void endCommonPrefixes(){
            insideCommonPrefixes = false;
        }

    }
    
    public class MultipartPartResultHandler extends SimpleHandler {
        private Integer partNumber = -1; // CopyPartResult doesn't include part number, use clearly invalid default
        private Date lastModified;
        private String etag;
        private Long size = -1l;  // CopyPartResult doesn't include size, use clearly invalid default

        public MultipartPartResultHandler(XMLReader xr) {
            super(xr);
        }

        public MultipartPart getMultipartPart() {
            return new MultipartPart(partNumber, lastModified, etag, size);
        }

        public void endPartNumber(String text) {
            this.partNumber = Integer.parseInt(text);
        }

        public void endLastModified(String text) throws ParseException {
            this.lastModified = ServiceUtils.parseIso8601Date(text);
        }

        public void endETag(String text) {
            this.etag = text;
        }

        public void endSize(String text) {
            this.size = Long.parseLong(text);
        }

        // </Part> represents end of a Part item in ListPartsResultHandler/Part
        public void endPart(String text) {
            returnControlToParentHandler();
        }
    }
    
    public class ListMultipartPartsResultHandler extends SimpleHandler {
        private final List<MultipartPart> parts = new ArrayList<MultipartPart>();
        private String bucketName = null;
        private String objectKey = null;
        private String uploadId = null;
        private CSOwner initiator = null;
        private CSOwner owner = null;
        private String storageClass = null;
        private String partNumberMarker = null;
        private String nextPartNumberMarker = null;
        private int maxParts = 1000;
        private boolean isTruncated = false;

        private boolean inInitiator = false;

        public ListMultipartPartsResultHandler(XMLReader xr) {
            super(xr);
        }

        public List<MultipartPart> getMultipartPartList() {
            return parts;
        }

        public boolean isTruncated() {
            return isTruncated;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }

        public String getUploadId() {
            return uploadId;
        }

        public CSOwner getInitiator() {
            return initiator;
        }

        public CSOwner getOwner() {
            return owner;
        }

        public String getStorageClass() {
            return storageClass;
        }

        public String getPartNumberMarker() {
            return partNumberMarker;
        }

        public String getNextPartNumberMarker() {
            return nextPartNumberMarker;
        }

        public int getMaxParts() {
            return maxParts;
        }

        public void startPart() {
            transferControlToHandler(new MultipartPartResultHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            if (childHandler instanceof MultipartPartResultHandler) {
                parts.add(
                    ((MultipartPartResultHandler) childHandler).getMultipartPart());
            } else {
                if (inInitiator) {
                    initiator = (CSOwner)((OwnerHandler)childHandler).getOwner();
                } else {
                    owner = (CSOwner)((OwnerHandler)childHandler).getOwner();
                }
            }
        }

        public void startInitiator() {
            inInitiator = true;
            transferControlToHandler(new OwnerHandler(xr));
        }

        public void startOwner() {
            inInitiator = false;
            transferControlToHandler(new OwnerHandler(xr));
        }

        public void endBucket(String text) {
            this.bucketName = text;
        }

        public void endKey(String text) {
            this.objectKey = text;
        }

        public void endStorageClass(String text) {
            this.storageClass = text;
        }

        public void endUploadId(String text) {
            this.uploadId = text;
        }

        public void endPartNumberMarker(String text) {
            this.partNumberMarker = text;
        }

        public void endNextPartNumberMarker(String text) {
            this.nextPartNumberMarker = text;
        }

        public void endMaxParts(String text) {
            this.maxParts = Integer.parseInt(text);
        }

        public void endIsTruncated(String text) {
            this.isTruncated = "true".equalsIgnoreCase(text);
        }
    }
    
    public class CompleteMultipartUploadResultHandler extends SimpleHandler {
        private String location;
        private String bucketName;
        private String objectKey;
        private String etag;

        private ServiceException serviceException = null;

        public CompleteMultipartUploadResultHandler(XMLReader xr) {
            super(xr);
        }

        public MultipartCompleted getMultipartCompleted() {
            return new MultipartCompleted(location, bucketName, objectKey, etag);
        }

        public ServiceException getServiceException() {
            return serviceException;
        }

        public void endLocation(String text) {
            this.location = text;
        }

        public void endBucket(String text) {
            this.bucketName = text;
        }

        public void endKey(String text) {
            this.objectKey = text;
        }

        public void endETag(String text) {
            this.etag = text;
        }

        public void startError() {
            transferControlToHandler(new CompleteMultipartUploadErrorHandler(xr));
        }

        @Override
        public void controlReturned(SimpleHandler childHandler) {
            this.serviceException = ((CompleteMultipartUploadErrorHandler)childHandler)
                .getServiceException();
        }
    }
    
    public class CompleteMultipartUploadErrorHandler extends SimpleHandler {
        private String code = null;
        private String message = null;
        private String etag = null;
        private Long minSizeAllowed = null;
        private Long proposedSize = null;
        private String hostId = null;
        private Integer partNumber = null;
        private String requestId = null;

        public CompleteMultipartUploadErrorHandler(XMLReader xr) {
            super(xr);
        }

        public ServiceException getServiceException() {
            String fullMessage = message
                + ": PartNumber=" + partNumber
                + ", MinSizeAllowed=" + minSizeAllowed
                + ", ProposedSize=" + proposedSize
                + ", ETag=" + etag;
            ServiceException e = new ServiceException(fullMessage);
            e.setErrorCode(code);
            e.setErrorMessage(message);
            e.setErrorHostId(hostId);
            e.setErrorRequestId(requestId);
            return e;
        }

        public void endCode(String text) {
            this.code = text;
        }

        public void endMessage(String text) {
            this.message = text;
        }

        public void endETag(String text) {
            this.etag = text;
        }

        public void endMinSizeAllowed(String text) {
            this.minSizeAllowed = Long.parseLong(text);
        }

        public void endProposedSize(String text) {
            this.proposedSize = Long.parseLong(text);
        }

        public void endHostId(String text) {
            this.hostId = text;
        }

        public void endPartNumber(String text) {
            this.partNumber = Integer.parseInt(text);
        }

        public void endRequestId(String text) {
            this.requestId = text;
        }

        public void endError(String text) {
            returnControlToParentHandler();
        }
    }
    
}
