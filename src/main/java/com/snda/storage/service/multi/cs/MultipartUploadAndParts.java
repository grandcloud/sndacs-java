package com.snda.storage.service.multi.cs;

import java.util.ArrayList;
import java.util.List;

import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartUpload;


/**
 * Packages together a MultipartUpload and a set of its component
 * CSObject parts.
 *
 */
public class MultipartUploadAndParts {

	private MultipartUpload multipartUpload;
    private List<CSObject> partObjects;
    private Integer partNumberOffset;

    public MultipartUploadAndParts(MultipartUpload multipartUpload, List<CSObject> partObjects,
        Integer partNumberOffset)
    {
        this.multipartUpload = multipartUpload;
        this.partObjects = partObjects;
        this.partNumberOffset = partNumberOffset;
    }

    public MultipartUploadAndParts(MultipartUpload multipartUpload, List<CSObject> partObjects)
    {
        this(multipartUpload, partObjects, 1);
    }

    public MultipartUploadAndParts(MultipartUpload multipartUpload)
    {
        this(multipartUpload, new ArrayList<CSObject>(), 1);
    }

    public void addPartObject(CSObject partObject) {
        this.partObjects.add(partObject);
    }

    public MultipartUpload getMultipartUpload() {
        return multipartUpload;
    }

    public List<CSObject> getPartObjects() {
        return partObjects;
    }

    public Integer getPartNumberOffset() {
        return partNumberOffset;
    }
}
