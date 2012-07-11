package com.snda.storage.service.multi.cs;

import com.snda.storage.service.multi.StorageServiceEventListener;


/**
 * Listener for events produced by {@link ThreadedCSService}.
 *
 */
public interface CSServiceEventListener extends StorageServiceEventListener {

	public void event(MultipartUploadsEvent event);
	
	public void event(MultipartStartsEvent event);
	
	public void event(MultipartCompletesEvent event);
	
}
