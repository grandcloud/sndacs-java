package com.snda.storage.service;

import com.snda.storage.service.model.StorageObject;

/**
 * Stores a "chunk" of StorageObjects returned from a list command - this particular chunk may or may
 * not include all the objects available in a bucket.
 *
 * This class contains an array of objects and a the last key name returned by a prior
 * call to the method {@link CSService#listObjectsChunked(String, String, String, long, String)}.
 *
 */
public class StorageObjectsChunk {
    protected String prefix = null;
    protected String delimiter = null;
    protected StorageObject[] objects = null;
    protected String[] commonPrefixes = null;
    protected String priorLastKey = null;

    public StorageObjectsChunk(String prefix, String delimiter, StorageObject[] objects,
        String[] commonPrefixes, String priorLastKey)
    {
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.objects = objects;
        this.commonPrefixes = commonPrefixes;
        this.priorLastKey = priorLastKey;
    }

    /**
     * @return
     * the objects in this chunk.
     */
    public StorageObject[] getObjects() {
        return objects;
    }

    /**
     * @return
     * the common prefixes in this chunk.
     */
    public String[] getCommonPrefixes() {
        return commonPrefixes;
    }


    /**
     * @return
     * the last key returned by the previous chunk if that chunk was incomplete, null otherwise.
     */
    public String getPriorLastKey() {
        return priorLastKey;
    }

    /**
     * @return
     * the prefix applied when this object chunk was generated. If no prefix was
     * applied, this method will return null.
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @return
     * the delimiter applied when this object chunk was generated. If no
     * delimiter was applied, this method will return null.
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * A convenience method to check whether a listing of objects is complete
     * (true) or there are more objects available (false). Just a synonym for
     * <code>{@link #getPriorLastKey()} == null</code>.
     *
     * @return
     * true if the listing is complete and there are no more unlisted
     * objects, false if follow-up requests will return more objects.
     */
    public boolean isListingComplete() {
        return (priorLastKey == null);
    }

}