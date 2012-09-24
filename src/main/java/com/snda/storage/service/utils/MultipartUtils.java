package com.snda.storage.service.utils;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.service.CSService;
import com.snda.storage.service.io.SegmentedRepeatableFileInputStream;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.multi.StorageServiceEventAdaptor;
import com.snda.storage.service.multi.cs.CSServiceEventAdaptor;
import com.snda.storage.service.multi.cs.CSServiceEventListener;
import com.snda.storage.service.multi.cs.MultipartStartsEvent;
import com.snda.storage.service.multi.cs.MultipartUploadAndParts;
import com.snda.storage.service.multi.cs.ThreadedCSService;
import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Tool to simplify working with the multipart uploads feature offered by
 * SNDA Cloud Storage.
 *
 */
@Deprecated
public class MultipartUtils {

	private static final Log log = LogFactory.getLog(MultipartUtils.class);

    /**
     * Minimum multipart upload part size: 5 MB.
     * NOTE: This minimum size does not apply to the last part in a
     * multipart upload, which may be 1 byte or larger.
     */
    public static final long MIN_PART_SIZE = 5 * (1024 * 1024);

    /**
     * Maximum object size supported by S3: 5 GB
     */
    public static final long MAX_OBJECT_SIZE = 5 * (1024 * 1024 * 1024);


    protected long maxPartSize = MAX_OBJECT_SIZE;
    
    /**
     * @param maxPartSize
     * the maximum size of objects that will be generated or upload by this instance,
     * must be between {@link #MIN_PART_SIZE} and {@link #MAX_OBJECT_SIZE}.
     */
    public MultipartUtils(long maxPartSize) {
        if (maxPartSize < MIN_PART_SIZE) {
            throw new IllegalArgumentException("Maximum part size parameter " + maxPartSize
                + " is less than the minimum legal part size " + MIN_PART_SIZE);
        }
        if (maxPartSize > MAX_OBJECT_SIZE) {
            throw new IllegalArgumentException("Maximum part size parameter " + maxPartSize
                + " is greater than the maximum legal upload object size " + MAX_OBJECT_SIZE);
        }
        this.maxPartSize = maxPartSize;
    }

    /**
     * Use default value for maximum part size: {@link #MAX_OBJECT_SIZE}.
     */
    public MultipartUtils() {
    }

    /**
     * @return
     * maximum part size as set in constructor.
     */
    public long getMaxPartSize() {
        return maxPartSize;
    }

    /**
     * @param file
     * @return
     * true if the given file is larger than the maximum part size defined in this instances.
     */
    public boolean isFileLargerThanMaxPartSize(File file) {
        return file.length() > maxPartSize;
    }
    
    /**
     * Split the given file into objects such that no object has a size greater than
     * the defined maximum part size. Each object uses a
     * {@link SegmentedRepeatableFileInputStream} input stream to manage its own
     * byte range within the underlying file.
     *
     * @param objectKey
     * the object key name to apply to all objects returned by this method.
     * @param file
     * a file to split into multiple parts.
     * @return
     * an ordered list of objects that can be uploaded as multipart parts to Cloud Storage to
     * re-constitute the given file in the service.
     *
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public List<CSObject> splitFileIntoObjectsByMaxPartSize(String objectKey, File file)
        throws IOException, NoSuchAlgorithmException {
    	long fileLength = file.length();
        long partCount = fileLength / maxPartSize + (fileLength % maxPartSize > 0 ? 1 : 0);

        if (log.isDebugEnabled()) {
            log.debug("Splitting file " + file.getAbsolutePath() + " of "
                + fileLength + " bytes into " + partCount
                + " object parts with a maximum part size of " + maxPartSize);
        }

        ArrayList<CSObject> multipartPartList = new ArrayList<CSObject>();
        SegmentedRepeatableFileInputStream segFIS = null;

        for (long offset = 0; offset < partCount; offset++) {
        	CSObject object = new CSObject(objectKey);
            if (offset < partCount - 1) {
                object.setContentLength(maxPartSize);
                segFIS = new SegmentedRepeatableFileInputStream(
                    file, offset * maxPartSize, maxPartSize);
            } else {
                // Last part, may not be full size.
                long partLength = fileLength % maxPartSize;
                // Handle edge-case where last part is exactly the size of maxPartSize
                if (partLength == 0) {
                    partLength = maxPartSize;
                }
                object.setContentLength(partLength);
                segFIS = new SegmentedRepeatableFileInputStream(
                    file, offset * maxPartSize, partLength);
            }
            object.setContentLength(segFIS.available());
            object.setDataInputStream(segFIS);

            // Calculate part's MD5 hash and reset stream
            object.setMd5Hash(ServiceUtils.computeMD5Hash(segFIS));
            object.setETag("\"" + object.getMd5HashAsHex() + "\"");
            segFIS.reset();

            multipartPartList.add(object);
        }
        return multipartPartList;
    }
    
    
    /**
     * Thread for creating/uploading an object that is part of a single multipart object.
     */
    public void uploadObjects(String bucketName, CSService csService,
            List<StorageObject> objectsForMultipartUpload,
        CSServiceEventListener eventListener) throws Exception {
        if (objectsForMultipartUpload == null || objectsForMultipartUpload.size() < 1) {
            return;
        }

        final List<MultipartUpload> multipartUploadList =
            new ArrayList<MultipartUpload>();
        final List<MultipartUploadAndParts> uploadAndPartsList =
            new ArrayList<MultipartUploadAndParts>();

        if (eventListener == null) {
            eventListener = new CSServiceEventAdaptor();
        }

        // Adaptor solely to capture newly-created MultipartUpload objects, which we
        // will need when it comes time to upload parts or complete the uploads.
        StorageServiceEventAdaptor captureMultipartUploadObjectsEventAdaptor =
            new CSServiceEventAdaptor() {
                @Override
                public void event(MultipartStartsEvent event) {
                    if (ServiceEvent.EVENT_IN_PROGRESS == event.getEventCode()) {
                        for (MultipartUpload upload: event.getStartedUploads()) {
                            multipartUploadList.add(upload);
                        }
                    }
                }
            };

        try {
            ThreadedCSService threadedCSService =
                new ThreadedCSService(csService, eventListener);
            threadedCSService.addServiceEventListener(
                captureMultipartUploadObjectsEventAdaptor);

            // Build map from object key to storage object
            final Map<String, StorageObject> objectsByKey =
                new HashMap<String, StorageObject>();
            for (StorageObject object: objectsForMultipartUpload) {
                objectsByKey.put(object.getKey(), object);
            }

            // Start all multipart uploads
            threadedCSService.multipartStartUploads(bucketName, objectsForMultipartUpload);
            throwServiceEventAdaptorErrorIfPresent(eventListener);

            // Build upload and part lists from new multipart uploads, where new
            // MultipartUpload objects were captured by this method's
            // captureMultipartUploadObjectsEventAdaptor)
            for (MultipartUpload upload: multipartUploadList) {
                StorageObject object = objectsByKey.get(upload.getObjectKey());
                if (object.getDataInputFile() == null) {
                	throw new ServiceException(
                            "multipartUpload method only supports file-based objects");
                }
                List<CSObject> partObjects = splitFileIntoObjectsByMaxPartSize(
                    upload.getObjectKey(),
                    object.getDataInputFile());
                uploadAndPartsList.add(
                    new MultipartUploadAndParts(upload, partObjects));
            }

            // Upload all parts for all multipart uploads
            threadedCSService.multipartUploadParts(uploadAndPartsList);
            throwServiceEventAdaptorErrorIfPresent(eventListener);

            // Complete all multipart uploads
            threadedCSService.multipartCompleteUploads(multipartUploadList);
            throwServiceEventAdaptorErrorIfPresent(eventListener);
        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("Multipart upload failed", e);
        }
    }

    protected void throwServiceEventAdaptorErrorIfPresent(
        CSServiceEventListener eventListener) throws Exception {
        if (eventListener instanceof CSServiceEventAdaptor) {
            ((CSServiceEventAdaptor)eventListener).throwErrorIfPresent();
        }
    }
    
}
