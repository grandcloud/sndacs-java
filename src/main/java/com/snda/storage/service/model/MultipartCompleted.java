package com.snda.storage.service.model;


/**
 * Represents a completed object resulting from a MultipartUpload operation.
 *
 */
@Deprecated
public class MultipartCompleted {
	private String location;
    private String bucketName;
    private String objectKey;
    private String etag;
    private String versionId;

    public MultipartCompleted(String location, String bucketName, String objectKey, String etag)
    {
        this.location = location;
        this.bucketName = bucketName;
        this.etag = etag;
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " ["
            + "location=" + getLocation()
            + ", bucketName=" + getBucketName()
            + ", objectKey=" + getObjectKey()
            + ", etag=" + getEtag()
            + (versionId != null ? ", etag=" + getEtag() : "")
            + "]";
    }

    public String getEtag() {
        return etag;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getLocation() {
        return location;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = versionId;
    }
    
}
