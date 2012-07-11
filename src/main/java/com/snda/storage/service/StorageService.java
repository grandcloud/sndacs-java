package com.snda.storage.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.service.model.CSBucket;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.StorageBucket;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.utils.RestUtils;
import com.snda.storage.service.utils.XmlResponsesSaxParser;

/**
 * A service that handles communication with a storage service, offering all the operations that
 * can be performed on generic storage services.
 * <p>
 * This class must be extended by implementation classes that perform the communication with
 * a specific service using a specific interface, such as REST or SOAP.
 * </p>
 * 
 * @author snda
 *
 */
public abstract class StorageService {
	public static final int BUCKET_STATUS__MY_BUCKET = 0;
	
	protected ProviderCredentials credentials = null;
	
	private boolean isHttpsOnly = false;
	
	private boolean isShutdown = false;
	
	/**
     * Construct a <code>StorageService</code> identified by the given user credentials.
     *
     * @param credentials
     * the user credentials.
     */
	public StorageService(ProviderCredentials credentials) {
		this.credentials = credentials;
	}
	
	public void shutdown() {
        this.isShutdown = true;
        this.shutdownImpl();
    }

	/**
     * @return true if the {@link #shutdown()} method has been used to shut down and
     * clean up this service. If this function returns true this service instance
     * can no longer be used to do work.
     */
	public boolean isShutdown() {
        return this.isShutdown;
    }
	
	/**
     * Whether to use secure HTTPS or insecure HTTP for communicating with a service,
     * as configured in isHttpsOnly.
     *
     * @return
     * true if this service should use only secure HTTPS communication channels.
     * If false, the non-secure HTTP protocol will be used.
     */
    public boolean isHttpsOnly() {
        return isHttpsOnly;
    }
	
	/**
     * @return
     * true if this service has {@link ProviderCredentials} identifying a user, false
     * if the service is acting as an anonymous user.
     */
	public boolean isAuthenticatedConnection() {
        return credentials != null;
    }
	
	/**
     * @return the credentials identifying the service user, or null for anonymous.
     */
	public ProviderCredentials getProviderCredentials() {
        return credentials;
    }
	
	/**
     * Lists the buckets belonging to the service user.
     *
     * @return
     * the list of buckets owned by the service user.
     */
	public StorageBucket[] listAllBuckets() {
		StorageBucket[] buckets = listAllBucketsImpl();
		return buckets;
	}
	
	/**
     * Lists the objects in a bucket.
     * <p>
     * The objects returned by this method contain only minimal information
     * such as the object's size, ETag, and LastModified timestamp. To retrieve
     * the objects' metadata you must perform follow-up <code>getObject</code>
     * or <code>getObjectDetails</code> operations.
     *
     * @param bucketName
     * the name of the bucket whose contents will be listed.
     * @return
     * the set of objects contained in a bucket.
     */
	public StorageObject[] listObjects(String bucketName) {
		return listObjects(bucketName, null, null, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE);
	}
	
	public StorageObject[] listObjects(String bucketName, String prefix, String delimiter) {
		return listObjects(bucketName, prefix, delimiter, Constants.DEFAULT_OBJECT_LIST_CHUNK_SIZE);
	}
	
	public StorageObject[] listObjects(String bucketName, String prefix, String delimiter,
			long maxListingLength) {
		StorageObject[] objects = listObjectsImpl(bucketName, prefix, delimiter, maxListingLength);
		return objects;
	}
	
	public StorageObject[] listObjects(String bucketName, String prefix, String delimiter,
			String marker, long maxListingLength) {
		StorageObject[] objects = listObjectsImpl(bucketName, prefix, delimiter, marker, maxListingLength);
		return objects;
	}
	
	/**
     * Lists the objects in a bucket matching a prefix, chunking the results into batches of
     * a given size, and returning each chunk separately. It is the responsibility of the caller
     * to building a complete bucket object listing by performing follow-up requests if necessary.
     * <p>
     * The objects returned by this method contain only minimal information
     * such as the object's size, ETag, and LastModified timestamp. To retrieve
     * the objects' metadata you must perform follow-up <code>getObject</code>
     * or <code>getObjectDetails</code> operations.
     * <p>
     * This method can be performed by anonymous services. Anonymous services
     * can list the contents of a publicly-readable bucket.
     *
     * @param bucketName
     * the name of the the bucket whose contents will be listed.
     * @param prefix
     * only objects with a key that starts with this prefix will be listed, may be null.
     * @param delimiter
     * only list objects with key names up to this delimiter, may be null.
     * @param maxListingLength
     * the maximum number of objects to include in each result chunk
     * @param priorLastKey
     * the last object key received in a prior call to this method. The next chunk of objects
     * listed will start with the next object in the bucket <b>after</b> this key name.
     * This parameter may be null, in which case the listing will start at the beginning of the
     * bucket's object contents.
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     */
    public StorageObjectsChunk listObjectsChunked(String bucketName, String prefix, String delimiter,
        long maxListingLength, String priorLastKey) {
        StorageObjectsChunk chunk = listObjectsChunkedImpl(
            bucketName, prefix, delimiter, maxListingLength, priorLastKey, false);
        return chunk;
    }

    /**
     * Lists the objects in a bucket matching a prefix and also returns the
     * common prefixes. Depending on the value of the completeListing
     * variable, this method can be set to automatically perform follow-up requests
     * to build a complete object listing, or to return only a partial listing.
     * <p>
     * The objects returned by this method contain only minimal information
     * such as the object's size, ETag, and LastModified timestamp. To retrieve
     * the objects' metadata you must perform follow-up <code>getObject</code>
     * or <code>getObjectDetails</code> operations.
     * <p>
     * This method can be performed by anonymous services. Anonymous services
     * can list the contents of a publicly-readable bucket.
     *
     * @param bucketName
     * the name of the the bucket whose contents will be listed.
     * @param prefix
     * only objects with a key that starts with this prefix will be listed, may be null.
     * @param delimiter
     * only objects with a key that starts with this prefix will be listed, may be null.
     * @param maxListingLength
     * the maximum number of objects to include in each result chunk
     * @param priorLastKey
     * the last object key received in a prior call to this method. The next chunk of objects
     * listed will start with the next object in the bucket <b>after</b> this key name.
     * This parameter may be null, in which case the listing will start at the beginning of the
     * bucket's object contents.
     * @param completeListing
     * if true, the service class will automatically perform follow-up requests to
     * build a complete bucket object listing.
     * @return
     * the set of objects contained in a bucket whose keys start with the given prefix.
     */
    public StorageObjectsChunk listObjectsChunked(String bucketName, String prefix, String delimiter,
        long maxListingLength, String priorLastKey, boolean completeListing) {
        StorageObjectsChunk chunk = listObjectsChunkedImpl(
            bucketName, prefix, delimiter, maxListingLength, priorLastKey, completeListing);
        return chunk;
    }
	
	/**
     * Creates a bucket.
     *
     * <b>Caution:</b> Performing this operation unnecessarily when a bucket already
     * exists may cause OperationAborted errors with the message "A conflicting conditional
     * operation is currently in progress against this resource.". To avoid this error, use the
     * {@link #getOrCreateBucket(String)} in situations where the bucket may already exist.
     *
     * @param bucketName
     * the name of the bucket to create.
     * @return
     * the created bucket object. <b>Note:</b> the object returned has minimal information about
     * the bucket that was created, including only the bucket's name.
     */
	public StorageBucket createBucket(String bucketName) {
		return createBucketImpl(bucketName, null);
	}
	
	/**
     * Create a bucket with the bucket object.
     * <p>
     * <b>Caution:</b> Performing this operation unnecessarily when a bucket already
     * exists may cause OperationAborted errors with the message "A conflicting conditional
     * operation is currently in progress against this resource.". To avoid this error, use the
     * {@link #getOrCreateBucket(String)} in situations where the bucket may already exist.
     *
     * @param bucket
     * the bucket to create.
     * @return
     * the created bucket object. <b>Note:</b> the object returned has minimal information about
     * the bucket that was created, including only the bucket's name.
     */
	public StorageBucket createBucket(StorageBucket bucket) {
		return createBucketImpl(bucket.getName(), null);
	}
	
	/**
     * Returns a bucket in your account by listing all your buckets
     * (using {@link #listAllBuckets()}), and looking for the named bucket in
     * this list.
     *
     * @param bucketName
     * @return
     * the bucket in your account, or null if you do not own the named bucket.
     */
	public StorageBucket getBucket(String bucketName) {
		StorageBucket[] existingBuckets = listAllBuckets();
        for (int i = 0; i < existingBuckets.length; i++) {
            if (existingBuckets[i].getName().equals(bucketName)) {
                return existingBuckets[i];
            }
        }
        return null;
	}
	
	/**
     * Returns a bucket in your account, and creates the bucket if
     * it does not yet exist.
     *
     * @param bucketName
     * the name of the bucket to retrieve or create.
     * @return
     * the bucket in your account.
     */
	public StorageBucket getOrCreateBucket(String bucketName) {
		StorageBucket bucket = getBucket(bucketName);
        if (bucket == null) {
            // Bucket does not exist in this user's account, create it.
            bucket = createBucket(bucketName);
        }
        return bucket;
	}
	
	/**
     * Deletes a bucket. Only the owner of a bucket may delete it.
     *
     * @param bucket
     * the bucket to delete.
     */
	public void deleteBucket(StorageBucket bucket) {
		deleteBucketImpl(bucket.getName());
	}
	
	/**
     * Deletes a bucket. Only the owner of a bucket may delete it.
     * <p>
     * This method cannot be performed by anonymous services.
     *
     * @param bucketName
     * the name of the bucket to delete.
     */
	public void deleteBucket(String bucketName) {
		deleteBucketImpl(bucketName);
	}
	
	/**
     * Convenience method to check whether an object exists in a bucket.
     *
     * @param bucketName
     * the name of the bucket containing the object.
     * @param objectKey
     * the key identifying the object.
     * @return
     * false if the object is not found in the bucket, true if the object
     * exists (although it may be inaccessible to you).
     */
	public boolean isObjectInBucket(String bucketName, String objectKey) {
		if (headObject(bucketName, objectKey) != null) {
			return true;
		}
		return false;
	}
	
	/**
     * Puts an object inside an existing bucket, creating a new object or overwriting
     * an existing one with the same key.
     *
     * @param bucketName
     * the name of the bucket inside which the object will be put.
     * @param object
     * the object containing all information that will be written to the service.
     * At very least this object must be valid. Beyond that it may contain: an input stream
     * with the object's data content and metadata.
     * <p>
     * <b>Note:</b> It is very important to set the object's Content-Length to match the size of the
     * data input stream when possible, as this can remove the need to read data into memory to
     * determine its size.
     *
     * @return
     * the object populated with any metadata.
     */
	public StorageObject putObject(String bucketName, StorageObject object) {
		return putObjectImpl(bucketName, object);
	}
	
	public Map<String, Object> copyObject(String sourceBucketName, String sourceObjectKey,
	        String destinationBucketName, StorageObject destinationObject, boolean replaceMetadata,
	        String[] ifMatchTags) {
		Map<String, Object> destinationMetadata =
            replaceMetadata ? destinationObject.getModifiableMetadata() : null;
		return copyObjectImpl(sourceBucketName, sourceObjectKey, destinationBucketName, destinationObject.getKey(), destinationMetadata, null);
	}
	
	public Map<String, Object> copyObject(String sourceBucketName, String sourceObjectKey,
	        String destinationBucketName, StorageObject destinationObject,
	        boolean replaceMetadata) {
		return copyObject(sourceBucketName, sourceObjectKey, destinationBucketName, destinationObject, replaceMetadata, null);
	}
	
	public Map<String, Object> moveObject(String sourceBucketName,
			String sourceObjectKey, String destinationBucketName,
			StorageObject destinationObject, boolean replaceMetadata) {
		Map<String, Object> copyResult = copyObject(sourceBucketName,
				sourceObjectKey, destinationBucketName, destinationObject,
				replaceMetadata);

		try {
			deleteObject(sourceBucketName, sourceObjectKey);
		} catch (Exception e) {
			copyResult.put("DeleteException", e);
		}
		return copyResult;
	}
	
	public Map<String, Object> renameObject(String bucketName,
			String sourceObjectKey, StorageObject destinationObject) {
		return moveObject(bucketName, sourceObjectKey, bucketName,
				destinationObject, true);
	}
	
	public Map<String, Object> updateObjectMetadata(String bucketName, StorageObject object) {
		return copyObject(bucketName, object.getKey(), bucketName, object, true);
	}
	
	/**
     * Deletes an object from a bucket.
     *
     * @param bucketName
     * the name of the bucket containing the object to be deleted.
     * @param objectKey
     * the key representing the object
     */
	public void deleteObject(String bucketName, String objectKey) {
		deleteObjectImpl(bucketName, objectKey);
	}
	
	/**
	 * Get object metadata.
	 * 
	 * @param bucketName
	 * the name of the bucket containing the object to be deleted.
	 * @param objectKey
	 * the key representing the object
	 * @return
	 * the object with the given key, including only general details and metadata (not the data
     * input stream)
	 */
	public StorageObject headObject(String bucketName, String objectKey) {
		return headObjectImpl(bucketName, objectKey);
	}
	
	/**
     * Returns an object representing the details and data of an item in a service,
     * without applying any preconditions.
     * <p>
     * This method can be performed by anonymous services. Anonymous services
     * can get a publicly-readable object.
     * <p>
     * <b>Important:</b> It is the caller's responsibility to close the object's data input stream.
     * The data stream should be consumed and closed as soon as is practical as network connections
     * may be held open until the streams are closed. Excessive unclosed streams can lead to
     * connection starvation.
     *
     * @param bucketName
     * the name of the bucket containing the object.
     * @param objectKey
     * the key identifying the object.
     * @return
     * the object with the given key, including the object's data input stream.
     */
	public StorageObject getObject(String bucketName, String objectKey) {
        return getObject(bucketName, objectKey, null, null, null);
    }
	
	public StorageObject getObject(String bucketName, String objectKey, String[] ifMatchTags,
			Long byteRangeStart, Long byteRangeEnd) {
		return getObjectImpl(bucketName, objectKey, ifMatchTags, byteRangeStart, byteRangeEnd);
	}
	
	/**
     * Lists objects in a bucket.
     *
     * <b>Implementation notes</b><p>
     * The implementation of this method is expected to return <b>all</b> the objects
     * in a bucket, not a subset. This may require repeating the list operation if the
     * first one doesn't include all the available objects, such as when the number of objects
     * is greater than <code>maxListingLength</code>.
     * <p>
     *
     * @param bucketName
     * @param prefix
     * only objects with a key that starts with this prefix will be listed, may be null.
     * @param delimiter
     * only list objects with key names up to this delimiter, may be null.
     * @param maxListingLength
     * @return
     * the objects in a bucket.
     *
     */
    protected abstract StorageObject[] listObjectsImpl(String bucketName, String prefix,
        String delimiter, long maxListingLength);
    
    protected abstract StorageObject[] listObjectsImpl(String bucketName, String prefix,
            String delimiter, String marker, long maxListingLength);
	
    /**
     * Lists objects in a bucket up to the maximum listing length specified.
     *
     * <p>
     * <b>Implementation notes</b>
     * The implementation of this method returns only as many objects as requested in the chunk
     * size. It is the responsibility of the caller to build a complete object listing from
     * multiple chunks, should this be necessary.
     * </p>
     *
     * @param bucketName
     * @param prefix
     * only objects with a key that starts with this prefix will be listed, may be null.
     * @param delimiter
     * only list objects with key names up to this delimiter, may be null.
     * @param maxListingLength
     * @param priorLastKey
     * @param completeListing
     */
    protected abstract StorageObjectsChunk listObjectsChunkedImpl(String bucketName, String prefix,
        String delimiter, long maxListingLength, String priorLastKey, boolean completeListing);
    
	/**
     * Creates a bucket.
     *
     * @param bucketName
     * the name of the bucket to create.
     * @param location
     * the geographical location where the bucket will be stored (if applicable for the target
     * service). A null string value will cause the bucket to be stored in the default location.
     * @return
     * the created bucket object, populated with all metadata made available by the creation operation.
     */
	protected abstract StorageBucket createBucketImpl(String bucketName, String location);
	
	protected abstract StorageBucket[] listAllBucketsImpl();
	
	protected abstract void deleteBucketImpl(String bucketName);
	
	protected abstract StorageObject putObjectImpl(String bucketName, StorageObject object);
	
	protected abstract Map<String, Object> copyObjectImpl(String sourceBucketName, String sourceObjectKey,
	        String destinationBucketName, String destinationObjectKey,
	        Map<String, Object> destinationMetadata,
	        String[] ifMatchTags);
	
	protected abstract void deleteObjectImpl(String bucketName, String objectKey);
	
	protected abstract StorageObject headObjectImpl(String bucketName, String objectKey);
	
	protected abstract StorageObject getObjectImpl(String bucketName, String objectKey, 
			String[] ifMatchTags, Long byteRangeStart, Long byteRangeEnd);
	
	
	protected StorageBucket newBucket() {
        return new CSBucket();
    }
	
	protected StorageObject newObject() {
        return new CSObject();
    }
	
	/**
     * Throws an exception if this service is anonymous (that is, it was created without
     * an {@link ProviderCredentials} object representing a user account.
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     */
	protected void assertAuthenticatedConnection(String action) {
        if (!isAuthenticatedConnection()) {
            throw new ServiceException(
                "The requested action cannot be performed with a non-authenticated service: "
                    + action);
        }
    }
	
	/**
     * Throws an exception if a bucket is null or contains a null/empty name.
     * @param bucket
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     */
	protected void assertValidBucket(StorageBucket bucket, String action) {
        if (bucket == null || bucket.getName() == null || bucket.getName().length() == 0) {
            throw new ServiceException("The action " + action
                + " cannot be performed with an invalid bucket: " + bucket);
        }
    }
	
	/**
     * Throws an exception if an object's key name is null or empty.
     * @param key
     * An object's key name.
     * @param action
     * the action being attempted which this assertion is applied, for debugging purposes.
     * @throws ServiceException
     */
    protected void assertValidObject(String key, String action) {
        if (key == null || key.length() == 0) {
            throw new ServiceException("The action " + action
                + " cannot be performed with an invalid object key name: " + key);
        }
    }
	
	public Map<String, Object> renameMetadataKeys(Map<String, Object> metadata) {
        Map<String, Object> convertedMetadata = new HashMap<String, Object>();
        // Add all meta-data headers.
        if (metadata != null) {
            for (Map.Entry<String, Object> entry: metadata.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                if (key.toLowerCase().equals("content-length"))
            		continue;
                
                if (!RestUtils.HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase(Locale.getDefault()))
                    && !key.startsWith(this.getRestHeaderPrefix()))
                {
                    key = this.getRestMetadataPrefix() + key;
                }
                convertedMetadata.put(key, value);
            }
        }
        return convertedMetadata;
    }
	
	public Date getCurrentTime() {
        return new Date(System.currentTimeMillis());
    }
	
	protected XmlResponsesSaxParser getXmlResponseSaxParser() {
        return new XmlResponsesSaxParser();
    }
	
	/**
     * @return
     * the REST header prefix used by the target service.
     */
	public abstract String getRestHeaderPrefix();
	
	/**
     * @return
     * GET parameter names that represent specific resources in the target
     * service, as opposed to representing REST operation "plumbing". For
     * example the "acl" parameter might be used to represent a resource's
     * access control list settings.
     */
	public abstract List<String> getResourceParameterNames();
	
	/**
     * @return
     * the REST header prefix used by the target service to identify
     * metadata information.
     */
	public abstract String getRestMetadataPrefix();
	/**
     * @return
     * the URL end-point of the target service.
     */
    public abstract String getEndpoint();
    public abstract String getSignedUrlEndpoint();
    protected abstract String getVirtualPath();
	protected abstract int getHttpPort();
    protected abstract int getHttpsPort();
    protected abstract boolean getDisableDnsBuckets();
    
    public void setHttpsOnly(boolean isHttpsOnly) {
    	this.isHttpsOnly = isHttpsOnly;
    }
    
    public boolean getHttpsOnly() {
    	return isHttpsOnly;
    }
    
    protected abstract String getSignatureIdentifier();
    
    protected abstract void shutdownImpl();
}
