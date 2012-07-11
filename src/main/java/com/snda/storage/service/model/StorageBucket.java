package com.snda.storage.service.model;

import java.util.Date;

/**
 * A generic storage bucket.
 * 
 * @author snda
 *
 */
public class StorageBucket extends BaseStorageItem {
	
	/**
     * Create a bucket without any name or location specified
     */
	public StorageBucket() {
        super();
    }
	
	/**
     * Create a bucket with a name.
     */
	public StorageBucket(String name) {
        super(name);
    }
	
	@Override
    public String toString() {
        return "StorageBucket [name=" + getName() + "] Metadata=" + getMetadataMap();
    }
	
	/**
     * @return
     * the bucket's creation date, or null if it is unknown.
     */
	public Date getCreationDate() {
        return (Date) getMetadata(METADATA_HEADER_CREATION_DATE);
    }
	
	/**
     * Sets the bucket's creation date - this should only be used internally by sdk
     * methods that retrieve information directly from a service.
     *
     * @param creationDate
     */
	public void setCreationDate(Date creationDate) {
        addMetadata(METADATA_HEADER_CREATION_DATE, creationDate);
    }
}
