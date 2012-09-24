package com.snda.storage.service.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Represents a MultipartUpload operation.
 *
 */
@Deprecated
public class MultipartUpload {

    private String uploadId;
    private String bucketName;
    private String objectKey;
    private Map<String, Object> metadata = null;
    private String storageClass;
    private CSOwner initiator;
    private CSOwner owner;
    private Date initiatedDate;
    private List<MultipartPart> multipartsPartsUploaded = new ArrayList<MultipartPart>();

    public MultipartUpload(String uploadId, String bucketName, String objectKey)
    {
        this.uploadId = uploadId;
        this.objectKey = objectKey;
        this.bucketName = bucketName;
    }

    public MultipartUpload(String uploadId, String objectKey, String storageClass,
    		CSOwner initiator, CSOwner owner, Date initiatedDate)
    {
        this.uploadId = uploadId;
        this.objectKey = objectKey;
        this.storageClass = storageClass;
        this.initiator = initiator;
        this.owner = owner;
        this.initiatedDate = initiatedDate;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " ["
            + "uploadId=" + getUploadId()
            + ", bucketName=" + getBucketName()
            + ", objectKey=" + getObjectKey()
            + (metadata != null ? ", metadata=" + getMetadata() : "")
            + (storageClass != null ? ", storageClass=" + getStorageClass() : "")
            + (initiator != null ? ", initiator=" + getInitiator() : "")
            + (owner != null ? ", owner=" + getOwner() : "")
            + (initiatedDate != null ? ", initiatedDate=" + getInitiatedDate() : "")
            + ", multipartsPartsUploaded=" + multipartsPartsUploaded
            + "]";
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

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public CSOwner getOwner() {
        return owner;
    }

    public Date getInitiatedDate() {
        return initiatedDate;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    public void setBucketName(String name) {
        this.bucketName = name;
    }

    public String getStorageClass() {
        return storageClass;
    }

    public CSOwner getInitiator() {
        return initiator;
    }

    public void addMultipartPartToUploadedList(MultipartPart part) {
        this.multipartsPartsUploaded.add(part);
    }

    public List<MultipartPart> getMultipartPartsUploaded() {
        return this.multipartsPartsUploaded;
    }
}
