package com.snda.storage.service.model;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.snda.storage.service.utils.Mimetypes;

/**
 * An Cloud Storage object.
 * 
 * @author snda
 *
 */
@Deprecated
public class CSObject extends StorageObject implements Cloneable {
	
	/**
     * Create an object without any associated information whatsoever.
     */
	public CSObject() {
        super();
    }
	
	/**
     * Create an object representing a file. The object is initialized with the file's name
     * as its key, the file's content as its data, a content type based on the file's extension
     * (see {@link Mimetypes}), and a content length matching the file's size.
     * The file's MD5 hash value is also calculated and provided to Cloud Storage, so the service
     * can verify that no data are corrupted in transit.
     * <p>
     * <b>NOTE:</b> The automatic calculation of a file's MD5 hash digest as performed by
     * this constructor could take some time for large files, or for many small ones.
     *
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param file
     * the file the object will represent. This file must exist and be readable.
     *
     * @throws IOException when an i/o error occurred reading the file
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
	public CSObject(CSBucket bucket, File file) throws NoSuchAlgorithmException, IOException {
        super(file);
        if (bucket != null) {
            this.bucketName = bucket.getName();
        }
    }
	
	/**
     * Create an object representing a file. The object is initialised with the file's name
     * as its key, the file's content as its data, a content type based on the file's extension
     * (see {@link Mimetypes}), and a content length matching the file's size.
     * The file's MD5 hash value is also calculated and provided to Cloud Storage, so the service
     * can verify that no data are corrupted in transit.
     * <p>
     * <b>NOTE:</b> The automatic calculation of a file's MD5 hash digest as performed by
     * this constructor could take some time for large files, or for many small ones.
     *
     * @param file
     * the file the object will represent. This file must exist and be readable.
     *
     * @throws IOException when an i/o error occurred reading the file
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
	public CSObject(File file) throws NoSuchAlgorithmException, IOException {
        super(file);
    }
	
	/**
     * Create an object representing text data. The object is initialized with the given
     * key, the given string as its data content (encoded as UTF-8), a content type of
     * <code>text/plain; charset=utf-8</code>, and a content length matching the
     * string's length.
     * The given string's MD5 hash value is also calculated and provided to Cloud Storage, so the service
     * can verify that no data are corrupted in transit.
     * <p>
     * <b>NOTE:</b> The automatic calculation of the MD5 hash digest as performed by
     * this constructor could take some time for large strings, or for many small ones.
     *
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param key
     * the key name for the object.
     * @param dataString
     * the text data the object will contain. Text data will be encoded as UTF-8.
     * This string cannot be null.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
	public CSObject(CSBucket bucket, String key, String dataString) throws NoSuchAlgorithmException, IOException
    {
        super(key, dataString);
        if (bucket != null) {
            this.bucketName = bucket.getName();
        }
    }
	
	/**
     * Create an object representing text data. The object is initialized with the given
     * key, the given string as its data content (encoded as UTF-8), a content type of
     * <code>text/plain; charset=utf-8</code>, and a content length matching the
     * string's length.
     * The given string's MD5 hash value is also calculated and provided to Cloud Storage, so the service
     * can verify that no data are corrupted in transit.
     * <p>
     * <b>NOTE:</b> The automatic calculation of the MD5 hash digest as performed by
     * this constructor could take some time for large strings, or for many small ones.
     *
     * @param key
     * the key name for the object.
     * @param dataString
     * the text data the object will contain. Text data will be encoded as UTF-8.
     * This string cannot be null.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
	public CSObject(String key, String dataString) throws NoSuchAlgorithmException, IOException
    {
        super(key, dataString);
    }
	
	/**
     * Create an object representing binary data. The object is initialized with the given
     * key, the bytes as its data content, a content type of
     * <code>application/octet-stream</code>, and a content length matching the
     * byte array's length.
     * The MD5 hash value of the byte data is also calculated and provided to the target
     * service, so the service can verify that no data are corrupted in transit.
     *
     * @param key
     * the key name for the object.
     * @param data
     * the byte data the object will contain, cannot be null.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException when this JRE doesn't support the MD5 hash algorithm
     */
	public CSObject(String key, byte[] data) throws NoSuchAlgorithmException, IOException
    {
        super(key, data);
    }
	
	/**
     * Create an object without any associated data, and no associated bucket.
     *
     * @param key
     * the key name for the object.
     */
	public CSObject(String key) {
		super(key);
	}
	
	/**
     * Create an object without any associated data.
     *
     * @param bucket
     * the bucket the object belongs to, or will be placed in.
     * @param key
     * the key name for the object.
     */
	public CSObject(CSBucket bucket, String key) {
        super(key);
        if (bucket != null) {
            this.bucketName = bucket.getName();
        }
    }
	
	@Override
    public String toString() {
        return "CSObject [key=" + getKey() + ", bucket=" + (bucketName == null ? "<Unknown>" : bucketName)
            + ", lastModified=" + getLastModifiedDate() + ", dataInputStream=" + dataInputStream
            + ", Metadata=" + getMetadataMap() + "]";
    }
	
	@Override
    public Object clone() {
		CSObject clone = (CSObject) super.clone();
		clone.setKey(getKey());
        clone.bucketName = bucketName;
        clone.dataInputStream = dataInputStream;
        clone.isMetadataComplete = isMetadataComplete;
        clone.dataInputFile = dataInputFile;
        clone.storageClass = storageClass;
        clone.addAllMetadata(getMetadataMap());
        return clone;
    }
	
	public static CSObject[] cast(StorageObject[] objects) {
        List<CSObject> results = new ArrayList<CSObject>();
        for (StorageObject object: objects) {
            results.add((CSObject)object);
        }
        return results.toArray(new CSObject[results.size()]);
    }
}
