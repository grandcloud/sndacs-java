package com.snda.storage.service.multi;

import com.snda.storage.service.multi.event.ServiceEvent;


/**
 * Simple implementation of {@link StorageServiceEventListener} to listen for events produced by
 * {@link ThreadedStorageService}.
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
public class StorageServiceEventAdaptor implements StorageServiceEventListener {

	private final Throwable t[] = new Throwable[1];
	
	protected void storeThrowable(ServiceEvent event) {
        if (t[0] == null && event.getEventCode() == ServiceEvent.EVENT_ERROR) {
            t[0] = event.getErrorCause();
        }
    }

    /**
     * @return
     * true if an event has resulted in an exception.
     */
    public boolean wasErrorThrown() {
        return t[0] != null;
    }

    /**
     * @return
     * the first error thrown by an event, or null if no error has been thrown.
     */
    public Throwable getErrorThrown() {
        return t[0];
    }

    /**
     * @throws Exception
     * throws first error thrown by an event, or does nothing if no error occurred.
     */
    public void throwErrorIfPresent() throws Exception {
        if (this.wasErrorThrown()) {
            Throwable thrown = this.getErrorThrown();
            if (thrown instanceof Exception) {
                throw (Exception) thrown;
            } else {
                throw new Exception(thrown);
            }
        }
    }
}
