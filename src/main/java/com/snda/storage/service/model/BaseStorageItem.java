package com.snda.storage.service.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class to represent storage items that can contain metadata: both objects and buckets.
 * 
 * @author snda
 *
 */
public abstract class BaseStorageItem {
	public static final String METADATA_HEADER_CREATION_DATE = "Date";
    public static final String METADATA_HEADER_LAST_MODIFIED_DATE = "Last-Modified";
    public static final String METADATA_HEADER_DATE = "Date";
    public static final String METADATA_HEADER_CONTENT_MD5 = "Content-MD5";
    public static final String METADATA_HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String METADATA_HEADER_CONTENT_TYPE = "Content-Type";
    public static final String METADATA_HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String METADATA_HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String METADATA_HEADER_CONTENT_LANGUAGE = "Content-Language";
    public static final String METADATA_HEADER_ETAG = "ETag";
    
    private String name = null;
    
    /**
     *  Map to metadata associated with this object.
     */
    private final Map<String, Object> metadata = new HashMap<String, Object>();
    
    protected BaseStorageItem(String name) {
        this.name = name;
    }
    
    protected BaseStorageItem() {
    }
    
    /**
     * @return
     * the name of the bucket.
     */
    public String getName() {
        return name;
    }
    
    /**
     * Set the name of the bucket.
     * @param name the name for the bucket or object
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return
     * an <b>immutable</b> map containing all the metadata associated with this object.
     */
    public Map<String, Object> getMetadataMap() {
        return Collections.unmodifiableMap(metadata);
    }
    
    /**
     * @param name
     * the metadata item name.
     *
     * @return
     * the value of the metadata with the given name, or null if no such metadata item exists.
     */
    public Object getMetadata(String name) {
        return this.metadata.get(name);
    }
    
    /**
     * @param name
     * the metadata item name.
     *
     * @return
     * true if this object contains a metadata item with the given name, false otherwise.
     */
    public boolean containsMetadata(String name) {
        return this.metadata.keySet().contains(name);
    }
    
    /**
     * Adds a metadata item to the object.
     *
     * @param name
     * the metadata item name.
     * @param value
     * the metadata item value.
     */
    public void addMetadata(String name, String value) {
        this.metadata.put(name, value);
    }
    
    /**
     * Adds a Date metadata item to the object.
     *
     * @param name
     * the metadata item name.
     * @param value
     * the metadata item's date value.
     */
    public void addMetadata(String name, Date value) {
        this.metadata.put(name, value);
    }
    
    /**
     * Adds all the items in the provided map to this object's metadata.
     *
     * @param metadata
     * metadata items to add.
     */
    public void addAllMetadata(Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
    }
    
    /**
     * Removes a metadata item from the object.
     *
     * @param name
     * the name of the metadata item to remove.
     */
    public void removeMetadata(String name) {
        this.metadata.remove(name);
    }
    
    /**
     * Removes all the metadata items associated with this object, then adds all the items
     * in the provided map. After performing this operation, the metadata list will contain
     * only those items in the provided map.
     *
     * @param metadata
     * metadata items to add.
     */
    public void replaceAllMetadata(Map<String, Object> metadata) {
        this.metadata.clear();
        addAllMetadata(metadata);
    }
}
