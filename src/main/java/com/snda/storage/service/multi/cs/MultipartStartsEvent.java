package com.snda.storage.service.multi.cs;

import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.multi.ThreadWatcher;
import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Multi-threaded service event fired by
 * {@link ThreadedCSService#multipartStartUploads(String, java.util.List)}.
 * <p>
 * EVENT_IN_PROGRESS events include an array of the {@link CSObject}s that have been created
 * since the last progress event was fired. These objects are available via
 * {@link #getStartedUploads()}.
 * <p>
 * EVENT_CANCELLED events include an array of the {@link CSObject}s that had not been created
 * before the operation was cancelled. These objects are available via
 * {@link #getCancelledObjects()}.
 *
 */
public class MultipartStartsEvent extends ServiceEvent {

	private MultipartUpload[] completedMultipartUploads = null;
	private StorageObject[] incompletedObjects = null;

	protected MultipartStartsEvent(int eventCode, Object uniqueOperationId) {
		super(eventCode, uniqueOperationId);
	}

	public static MultipartStartsEvent newErrorEvent(Throwable t,
			Object uniqueOperationId) {
		MultipartStartsEvent event = new MultipartStartsEvent(EVENT_ERROR,
				uniqueOperationId);
		event.setErrorCause(t);
		return event;
	}
	
	public static MultipartStartsEvent newStartedEvent(ThreadWatcher threadWatcher, 
			Object uniqueOperationId) {
		MultipartStartsEvent event = new MultipartStartsEvent(EVENT_STARTED, 
				uniqueOperationId);
		event.setThreadWatcher(threadWatcher);
		return event;
	}
	
	public static MultipartStartsEvent newInProgressEvent(ThreadWatcher threadWatcher, 
			MultipartUpload[] completedMultipartUploads, Object uniqueOperationId) {
		MultipartStartsEvent event = new MultipartStartsEvent(EVENT_IN_PROGRESS,
				uniqueOperationId);
		event.setThreadWatcher(threadWatcher);
		event.setCompletedUploads(completedMultipartUploads);
		return event;
	}
	
	public static MultipartStartsEvent newCompletedEvent(Object uniqueOperationId) {
		MultipartStartsEvent event = new MultipartStartsEvent(EVENT_COMPLETED,
				uniqueOperationId);
		return event;
	}
	
	public static MultipartStartsEvent newCancelledEvent(StorageObject[] incompletedObjects,
			Object uniqueOperationId) {
		MultipartStartsEvent event = new MultipartStartsEvent(EVENT_CANCELLED,
				uniqueOperationId);
		event.setIncompletedObjects(incompletedObjects);
		return event;
	}
	
	public static MultipartStartsEvent newIgnoredErrorsEvent(ThreadWatcher threadWatcher,
	        Throwable[] ignoredErrors, Object uniqueOperationId) {
        MultipartStartsEvent event = new MultipartStartsEvent(EVENT_IGNORED_ERRORS, uniqueOperationId);
        event.setIgnoredErrors(ignoredErrors);
        return event;
    }
	
	private void setIncompletedObjects(StorageObject[] objects) {
        this.incompletedObjects = objects;
    }
	
	private void setCompletedUploads(MultipartUpload[] uploads) {
        this.completedMultipartUploads = uploads;
    }
	
	/**
     * @return
     * the {@link MultipartUpload}s that have been started since the last progress event was fired.
     * @throws IllegalStateException
     * created objects are only available from EVENT_IN_PROGRESS events.
     */
	public MultipartUpload[] getStartedUploads() throws IllegalStateException {
        if (getEventCode() != EVENT_IN_PROGRESS) {
            throw new IllegalStateException("Started Objects are only available from EVENT_IN_PROGRESS events");
        }
        return completedMultipartUploads;
    }
	
	/**
     * @return
     * the {@link StorageObject}s that were not created before the operation was cancelled.
     * @throws IllegalStateException
     * cancelled objects are only available from EVENT_CANCELLED events.
     */
	public StorageObject[] getCancelledObjects() throws IllegalStateException {
        if (getEventCode() != EVENT_CANCELLED) {
            throw new IllegalStateException("Cancelled Objects are  only available from EVENT_CANCELLED events");
        }
        return incompletedObjects;
    }

}
