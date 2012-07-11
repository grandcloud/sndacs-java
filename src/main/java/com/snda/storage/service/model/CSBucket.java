package com.snda.storage.service.model;

import java.util.ArrayList;
import java.util.List;

import com.snda.storage.service.Constants;

/**
 * Represents an Cloud Storage bucket.
 * 
 * @author snda
 *
 */
public class CSBucket extends StorageBucket {
	
	private String location = Constants.CS_DEFAULT_LOCATION;
	private boolean isLocationKnown = false;
	
	/**
     * Create a bucket without any name or location specified
     */
	public CSBucket() {
	}
	
	/**
     * Create a bucket with a name. All buckets in Cloud Storage share a single namespace,
     * so choose a unique name for your bucket.
     * @param name the name for the bucket
     */
	public CSBucket(String name) {
		super(name);
	}
	
	/**
     * Create a bucket with a name and a location. All buckets in Cloud Storage share a single namespace,
     * so choose a unique name for your bucket.
     * @param name the name for the bucket
     * @param location A string representing the location.
     */
	public CSBucket(String name, String location) {
        this(name);
        this.location = location;
        this.isLocationKnown = true;
    }
	
	@Override
    public String toString() {
        return "CSBucket [name=" + getName() +
            ",location=" + getLocation() +
            ",creationDate=" + getCreationDate()
            + "] Metadata=" + getMetadataMap();
    }
	
	/**
     * Set's the bucket's location. This method should only be used internally by
     * sdk methods that retrieve information directly from Cloud Storage.
     *
     * @param location
     * A string representing the location.
     */
	public void setLocation(String location) {
        this.location = location;
        this.isLocationKnown = true;
    }

	/**
     * @return
     * true if this object knows the bucket's location, false otherwise.
     */
	public boolean isLocationKnown() {
        return this.isLocationKnown;
    }
	
	/**
     * @return
     * the bucket's location represented as a string.
     */
	public String getLocation() {
        return location;
    }
	
	public static CSBucket[] cast(StorageBucket[] buckets) {
        List<CSBucket> results = new ArrayList<CSBucket>();
        for (StorageBucket bucket: buckets) {
            results.add((CSBucket)bucket);
        }
        return results.toArray(new CSBucket[results.size()]);
    }
}
