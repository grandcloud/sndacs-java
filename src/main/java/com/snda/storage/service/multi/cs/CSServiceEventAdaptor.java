package com.snda.storage.service.multi.cs;

import com.snda.storage.service.multi.StorageServiceEventAdaptor;
import com.snda.storage.service.multi.ThreadedStorageService;

/**
 * Simple implementation of {@link CSServiceEventListener} to listen for events produced by
 * {@link ThreadedCSService} and {@link ThreadedStorageService}.
 * <p>
 * By default this adaptor does nothing but store the first Error event it comes across, if any,
 * and make it available through {@link #getErrorThrown()}.
 * </p>
 * <p>
 * The behaviour of this class can be specialised by over-riding the appropriate
 * <tt>event</tt> methods, though always be sure to call the <b>super</b>
 * version of these methods if you are relying on the default error-trapping functions of this
 * class.
 * </p>
 *
 */
public class CSServiceEventAdaptor extends StorageServiceEventAdaptor implements
		CSServiceEventListener {
	
	@Override
	public void event(MultipartStartsEvent event) {
        storeThrowable(event);
    }

	@Override
	public void event(MultipartUploadsEvent event) {
		storeThrowable(event);
	}

	@Override
	public void event(MultipartCompletesEvent event) {
		storeThrowable(event);
	}
	
}
