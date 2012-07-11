package com.snda.storage.service.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;


/**
 * Represents a Part of a MultipartUpload operation.
 *
 */
public class MultipartPart {
	private final Integer partNumber;
    private final Date lastModified;
    private final String etag;
    private final Long size;

    public MultipartPart(Integer partNumber, Date lastModified, String etag, Long size)
    {
        if (partNumber == null){
            throw new IllegalArgumentException("Null part number not allowed.");
        }
        if (lastModified == null){
            throw new IllegalArgumentException("Null last modified not allowed.");
        }
        if (etag == null){
            throw new IllegalArgumentException("Null etag not allowed.");
        }
        if (size == null){
            throw new IllegalArgumentException("Null size not allowed.");
        }
        this.partNumber = partNumber;
        this.lastModified = lastModified;
        this.size = size;
        // Strip quote characters from etag value
        this.etag = etag.replaceAll("\"", "");
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other instanceof MultipartPart) {
            MultipartPart p = (MultipartPart) other;
            return Arrays.equals(
                    new Object[]{ partNumber, lastModified, size},
                    new Object[]{ p.partNumber, p.lastModified, p.size} ) &&
                    sameEtag(p.etag);
        }
        return false;
    }

    private boolean sameEtag(String pEtag){
        if (etag==pEtag){
            return true;
        }
        if (etag == null){
            return false;
        }
        return etag.equals(pEtag) || ("\""+etag+"\"").equals(pEtag);
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " ["
            + "partNumber=" + getPartNumber()
            + ", lastModified=" + getLastModified()
            + ", etag=" + getEtag()
            + ", size=" + getSize()
            + "]";
    }

    public String getEtag() {
        return etag;
    }

    public Long getSize() {
        return size;
    }

    public Integer getPartNumber() {
        return partNumber;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public static class PartNumberComparator implements Comparator<MultipartPart> {
        public int compare(MultipartPart o1, MultipartPart o2){
            if (o1 == o2){
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.getPartNumber().compareTo(o2.getPartNumber());
        }
    } //PartNumberComparator
}
