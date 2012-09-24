package com.snda.storage.service.multi.cs;

import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.multi.ThreadWatcher;
import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Multi-threaded service event fired by
 * {@link ThreadedCSService#multipartUploadParts(java.util.List)}.
 * <p>
 * EVENT_IN_PROGRESS events include an array of the {@link StorageObject}s that have been created
 * since the last progress event was fired. These objects are available via
 * {@link #getUploadedObjects()}.
 * <p>
 * EVENT_CANCELLED events include an array of the {@link StorageObject}s that had not been created
 * before the operation was cancelled. These objects are available via
 * {@link #getCancelledObjects()}.
 *
 */
public class MultipartUploadsEvent extends ServiceEvent {
	
	private StorageObject[] objects = null;

	protected MultipartUploadsEvent(int eventCode, Object uniqueOperationId) {
		super(eventCode, uniqueOperationId);
	}
	
	public static MultipartUploadsEvent newErrorEvent(Throwable t,
			Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(EVENT_ERROR,
				uniqueOperationId);
		event.setErrorCause(t);
		return event;
	}

	public static MultipartUploadsEvent newStartedEvent(
			ThreadWatcher threadWatcher, Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(EVENT_STARTED,
				uniqueOperationId);
		event.setThreadWatcher(threadWatcher);
		return event;
	}

	public static MultipartUploadsEvent newInProgressEvent(
			ThreadWatcher threadWatcher, StorageObject[] completedObjects,
			Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(
				EVENT_IN_PROGRESS, uniqueOperationId);
		event.setThreadWatcher(threadWatcher);
		event.setObjects(completedObjects);
		return event;
	}

	public static MultipartUploadsEvent newCompletedEvent(
			Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(
				EVENT_COMPLETED, uniqueOperationId);
		return event;
	}

	public static MultipartUploadsEvent newCancelledEvent(
			StorageObject[] incompletedObjects, Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(
				EVENT_CANCELLED, uniqueOperationId);
		event.setObjects(incompletedObjects);
		return event;
	}

	public static MultipartUploadsEvent newIgnoredErrorsEvent(
			ThreadWatcher threadWatcher, Throwable[] ignoredErrors,
			Object uniqueOperationId) {
		MultipartUploadsEvent event = new MultipartUploadsEvent(
				EVENT_IGNORED_ERRORS, uniqueOperationId);
		event.setIgnoredErrors(ignoredErrors);
		return event;
	}

	private void setObjects(StorageObject[] objects) {
		this.objects = objects;
	}

    /**
     * @return
     * the {@link StorageObject}s that have been uploaded since the last progress event was fired.
     * @throws IllegalStateException
     * created objects are only available from EVENT_IN_PROGRESS events.
     */
	public StorageObject[] getUploadedObjects() throws IllegalStateException {
		if (getEventCode() != EVENT_IN_PROGRESS) {
			throw new IllegalStateException(
					"Created Objects are only available from EVENT_IN_PROGRESS events");
		}
		return objects;
	}

    /**
     * @return
     * the {@link StorageObject}s that were not created before the operation was cancelled.
     * @throws IllegalStateException
     * cancelled objects are only available from EVENT_CANCELLED events.
     */
	public StorageObject[] getCancelledObjects() throws IllegalStateException {
		if (getEventCode() != EVENT_CANCELLED) {
			throw new IllegalStateException(
					"Cancelled Objects are  only available from EVENT_CANCELLED events");
		}
		return objects;
	}

}
