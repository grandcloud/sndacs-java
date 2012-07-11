package com.snda.storage.service.multi;

import java.util.EventListener;


/**
 * Interface implemented by multi-threaded operations that can be cancelled prior to finishing.
 *
 */
public interface CancelEventTrigger extends EventListener {

	/**
     * Triggers a cancellation of some operation.
     *
     * @param eventSource
     * the object source that triggered the cancellation, useful for logging purposes.
     *
     */
    public abstract void cancelTask(Object eventSource);
    
}
