package com.snda.storage.service.multi.cs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.service.CSService;
import com.snda.storage.service.StorageService;
import com.snda.storage.service.io.BytesProgressWatcher;
import com.snda.storage.service.io.InterruptableInputStream;
import com.snda.storage.service.io.ProgressMonitoredInputStream;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartCompleted;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.multi.StorageServiceEventListener;
import com.snda.storage.service.multi.ThreadWatcher;
import com.snda.storage.service.multi.ThreadedStorageService;
import com.snda.storage.service.multi.event.ServiceEvent;

public class ThreadedCSService extends ThreadedStorageService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedCSService.class);

	public ThreadedCSService(StorageService service,
			StorageServiceEventListener listener) throws ServiceException {
		super(service, listener);
	}
	
	@Override
	public void fireServiceEvent(ServiceEvent event) {
		if (serviceEventListeners.size() == 0) {
            if (LOGGER.isWarnEnabled()) {
                LOGGER.warn("ThreadedCSService invoked without any StorageServiceEventListener objects, this is dangerous!");
            }
        }
        for (StorageServiceEventListener listener: this.serviceEventListeners) {
            if (listener instanceof CSServiceEventListener) {
                if (event instanceof MultipartUploadsEvent) {
                    ((CSServiceEventListener)listener).event((MultipartUploadsEvent) event);
                } else if (event instanceof MultipartStartsEvent) {
                    ((CSServiceEventListener)listener).event((MultipartStartsEvent) event);
                } else if (event instanceof MultipartCompletesEvent) {
                    ((CSServiceEventListener)listener).event((MultipartCompletesEvent) event);
                } else {
                    super.fireServiceEvent(event);
                }
            } else {
                super.fireServiceEvent(event);
            }
        }
	}
	
	private void assertIsCSService() {
        if (!(storageService instanceof CSService)) {
            throw new IllegalStateException(
                "Multipart uploads are only available in , Grand Cloud Storage" +
                "you must use the CSService implementation of StorageService");
        }
    }
	
	/**
     * Starts multiple multipart uploads and sends {@link MultipartStartsEvent} notification events.
     *
     * @param bucketName
     * the target bucket.
     * @param objects
     * a list of objects representing the overall multipart object.
     *
     * @return
     * true if all the threaded tasks completed successfully, false otherwise.
     */
	public boolean multipartStartUploads(final String bucketName, final List<StorageObject> objects)
    {
        assertIsCSService();
        final List<StorageObject> incompletedObjectsList = new ArrayList<StorageObject>();
        final Object uniqueOperationId = new Object(); // Special object used to identify this operation.
        final boolean[] success = new boolean[] {true};

        // Start all queries in the background.
        List<MultipartStartRunnable> runnableList = new ArrayList<MultipartStartRunnable>();
        for (StorageObject object: objects) {
            incompletedObjectsList.add(object);
            runnableList.add(new MultipartStartRunnable(bucketName, object));
        }

        // Wait for threads to finish, or be canceled.
        ThreadWatcher threadWatcher = new ThreadWatcher(runnableList.size());
        (new ThreadGroupManager(runnableList.toArray(new MultipartStartRunnable[] {}),
            threadWatcher, true)
        {
            @Override
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(MultipartStartsEvent.newStartedEvent(threadWatcher, uniqueOperationId));
            }
            @Override
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                MultipartUpload[] completedObjects = (MultipartUpload[]) completedResults
                    .toArray(new MultipartUpload[completedResults.size()]);
                // Hack way to remove completed objects from incomplete list
                List<StorageObject> completedStorageObjects = new ArrayList<StorageObject>();
                for (MultipartUpload upload: completedObjects) {
                    for (StorageObject object: incompletedObjectsList) {
                        if (object.getKey().equals(upload.getObjectKey())) {
                            completedStorageObjects.add(object);
                        }
                    }
                }
                incompletedObjectsList.removeAll(completedStorageObjects);

                fireServiceEvent(MultipartStartsEvent.newInProgressEvent(threadWatcher,
                    completedObjects, uniqueOperationId));
            }
            @Override
            public void fireCancelEvent() {
                StorageObject[] incompletedObjects = incompletedObjectsList
                    .toArray(new StorageObject[incompletedObjectsList.size()]);
                success[0] = false;
                fireServiceEvent(MultipartStartsEvent.newCancelledEvent(incompletedObjects, uniqueOperationId));
            }
            @Override
            public void fireCompletedEvent() {
                fireServiceEvent(MultipartStartsEvent.newCompletedEvent(uniqueOperationId));
            }
            @Override
            public void fireErrorEvent(Throwable throwable) {
                success[0] = false;
                fireServiceEvent(MultipartStartsEvent.newErrorEvent(throwable, uniqueOperationId));
            }
            @Override
            public void fireIgnoredErrorsEvent(ThreadWatcher threadWatcher, Throwable[] ignoredErrors) {
                success[0] = false;
                fireServiceEvent(MultipartStartsEvent.newIgnoredErrorsEvent(threadWatcher, ignoredErrors, uniqueOperationId));
            }
        }).run();

        return success[0];
    }
	
	/**
     * Completes multiple multipart uploads and sends {@link MultipartCompletesEvent} notification events.
     *
     * @param multipartUploads
     * a list of objects representing the multipart-uploads that will be completed.
     *
     * @return
     * true if all the threaded tasks completed successfully, false otherwise.
     */
    public boolean multipartCompleteUploads(final List<MultipartUpload> multipartUploads)
    {
        assertIsCSService();
        final List<MultipartUpload> incompletedObjectsList = new ArrayList<MultipartUpload>();
        final Object uniqueOperationId = new Object(); // Special object used to identify this operation.
        final boolean[] success = new boolean[] {true};

        // Start all queries in the background.
        List<MultipartCompleteRunnable> runnableList = new ArrayList<MultipartCompleteRunnable>();
        for (MultipartUpload multipartUpload: multipartUploads) {
            incompletedObjectsList.add(multipartUpload);
            runnableList.add(new MultipartCompleteRunnable(multipartUpload));
        }

        // Wait for threads to finish, or be canceled.
        ThreadWatcher threadWatcher = new ThreadWatcher(runnableList.size());
        (new ThreadGroupManager(runnableList.toArray(new MultipartCompleteRunnable[] {}),
            threadWatcher, true)
        {
            @Override
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(MultipartCompletesEvent.newStartedEvent(threadWatcher, uniqueOperationId));
            }
            @Override
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompletedObjectsList.removeAll(completedResults);
                MultipartCompleted[] completedObjects = (MultipartCompleted[]) completedResults
                    .toArray(new MultipartCompleted[completedResults.size()]);
                fireServiceEvent(MultipartCompletesEvent.newInProgressEvent(threadWatcher,
                    completedObjects, uniqueOperationId));
            }
            @Override
            public void fireCancelEvent() {
                MultipartUpload[] incompletedObjects = incompletedObjectsList
                    .toArray(new MultipartUpload[incompletedObjectsList.size()]);
                success[0] = false;
                fireServiceEvent(MultipartCompletesEvent.newCancelledEvent(incompletedObjects, uniqueOperationId));
            }
            @Override
            public void fireCompletedEvent() {
                fireServiceEvent(MultipartCompletesEvent.newCompletedEvent(uniqueOperationId));
            }
            @Override
            public void fireErrorEvent(Throwable throwable) {
                success[0] = false;
                fireServiceEvent(MultipartCompletesEvent.newErrorEvent(throwable, uniqueOperationId));
            }
            @Override
            public void fireIgnoredErrorsEvent(ThreadWatcher threadWatcher, Throwable[] ignoredErrors) {
                success[0] = false;
                fireServiceEvent(MultipartCompletesEvent.newIgnoredErrorsEvent(threadWatcher, ignoredErrors, uniqueOperationId));
            }
        }).run();

        return success[0];
    }
	
	
	/**
     * Uploads multiple objects that will constitute a single final object,
     * and sends {@link MultipartUploadsEvent} notification events.
     *
     * @param uploadAndPartsList
     * list of wrapper objects containing a previously-started MultipartUpload and a
     * list of objects representing the parts that will make up the final object.
     *
     * @return
     * true if all the threaded tasks completed successfully, false otherwise.
     */
    public boolean multipartUploadParts(List<MultipartUploadAndParts> uploadAndPartsList)
    {
        assertIsCSService();
        final List<StorageObject> incompletedObjectsList = new ArrayList<StorageObject>();
        final List<BytesProgressWatcher> progressWatchers = new ArrayList<BytesProgressWatcher>();
        final Object uniqueOperationId = new Object(); // Special object used to identify this operation.
        final boolean[] success = new boolean[] {true};

        // Start all queries in the background.
        List<MultipartUploadObjectRunnable> runnableList =
            new ArrayList<MultipartUploadObjectRunnable>();
        for (MultipartUploadAndParts multipartUploadAndParts: uploadAndPartsList) {
            int partNumber = multipartUploadAndParts.getPartNumberOffset();
            for (CSObject partObject: multipartUploadAndParts.getPartObjects()) {
                incompletedObjectsList.add(partObject);
                BytesProgressWatcher progressMonitor = new BytesProgressWatcher(partObject.getContentLength());
                runnableList.add(new MultipartUploadObjectRunnable(
                    multipartUploadAndParts.getMultipartUpload(),
                    partNumber, partObject, progressMonitor));
                progressWatchers.add(progressMonitor);
                partNumber++;
            }
        }

        // Wait for threads to finish, or be canceled.
        ThreadWatcher threadWatcher = new ThreadWatcher(
            progressWatchers.toArray(new BytesProgressWatcher[progressWatchers.size()]));
        (new ThreadGroupManager(runnableList.toArray(new MultipartUploadObjectRunnable[] {}),
            threadWatcher, false)
        {
            @Override
            public void fireStartEvent(ThreadWatcher threadWatcher) {
                fireServiceEvent(MultipartUploadsEvent.newStartedEvent(threadWatcher, uniqueOperationId));
            }
            @Override
            public void fireProgressEvent(ThreadWatcher threadWatcher, List completedResults) {
                incompletedObjectsList.removeAll(completedResults);
                StorageObject[] completedObjects = (StorageObject[]) completedResults
                    .toArray(new StorageObject[completedResults.size()]);
                fireServiceEvent(MultipartUploadsEvent.newInProgressEvent(threadWatcher,
                    completedObjects, uniqueOperationId));
            }
            @Override
            public void fireCancelEvent() {
                StorageObject[] incompletedObjects = incompletedObjectsList
                    .toArray(new StorageObject[incompletedObjectsList.size()]);
                success[0] = false;
                fireServiceEvent(MultipartUploadsEvent.newCancelledEvent(incompletedObjects, uniqueOperationId));
            }
            @Override
            public void fireCompletedEvent() {
                fireServiceEvent(MultipartUploadsEvent.newCompletedEvent(uniqueOperationId));
            }
            @Override
            public void fireErrorEvent(Throwable throwable) {
                success[0] = false;
                fireServiceEvent(MultipartUploadsEvent.newErrorEvent(throwable, uniqueOperationId));
            }
            @Override
            public void fireIgnoredErrorsEvent(ThreadWatcher threadWatcher, Throwable[] ignoredErrors) {
                success[0] = false;
                fireServiceEvent(MultipartUploadsEvent.newIgnoredErrorsEvent(threadWatcher, ignoredErrors, uniqueOperationId));
            }
        }).run();

        return success[0];
    }
	
	/**
     * Thread for starting a single multipart object upload.
     */
    private class MultipartStartRunnable extends AbstractRunnable {
        private String bucketName = null;
        private StorageObject object = null;

        private Object result = null;

        public MultipartStartRunnable(String bucketName, StorageObject object)
        {
            this.bucketName = bucketName;
            this.object = object;
        }

        public void run() {
            try {
                result = ((CSService)storageService).multipartStartUpload(bucketName,
                    object.getKey(), object.getMetadataMap());
            } catch (ServiceException e) {
                result = e;
            }
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public void forceInterruptCalled() {
            // operation cannot be interrupted, no-op
        }
    }
    
    /**
     * Thread for completing a single multipart object upload.
     */
    private class MultipartCompleteRunnable extends AbstractRunnable {
        private MultipartUpload multipartUpload = null;

        private Object result = null;

        public MultipartCompleteRunnable(MultipartUpload multipartUpload)
        {
            this.multipartUpload = multipartUpload;
        }

        public void run() {
            try {
                result = ((CSService)storageService).multipartCompleteUpload(multipartUpload);
            } catch (ServiceException e) {
                result = e;
            }
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public void forceInterruptCalled() {
            // operation cannot be interrupted, no-op
        }
    }
    
    
    /**
     * Thread for creating/uploading an object that is part of a single multipart object.
     * The upload of any object data is monitored with a
     * {@link ProgressMonitoredInputStream} and can be can cancelled as the input stream is wrapped in
     * an {@link InterruptableInputStream}.
     */
    private class MultipartUploadObjectRunnable extends AbstractRunnable {
        private MultipartUpload multipartUpload = null;
        private Integer partNumber = null;
        private CSObject object = null;
        private InterruptableInputStream interruptableInputStream = null;
        private BytesProgressWatcher progressMonitor = null;

        private Object result = null;

        public MultipartUploadObjectRunnable(MultipartUpload multipartUpload,
            Integer partNumber, CSObject object, BytesProgressWatcher progressMonitor)
        {
            this.multipartUpload = multipartUpload;
            this.partNumber = partNumber;
            this.object = object;
            this.progressMonitor = progressMonitor;
        }

        public void run() {
            try {
                if (object.getDataInputStream() != null) {
                    interruptableInputStream = new InterruptableInputStream(object.getDataInputStream());
                    ProgressMonitoredInputStream pmInputStream = new ProgressMonitoredInputStream(
                        interruptableInputStream, progressMonitor);
                    object.setDataInputStream(pmInputStream);
                }
                ((CSService)storageService).multipartUploadPart(
                    multipartUpload, partNumber, object);
                result = object;
            } catch (ServiceException e) {
                result = e;
            }
        }

        @Override
        public Object getResult() {
            return result;
        }

        @Override
        public void forceInterruptCalled() {
            if (interruptableInputStream != null) {
                interruptableInputStream.interrupt();
            }
        }
    }


}
