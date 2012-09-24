package com.snda.storage.service.io;

import java.io.InputStream;


/**
 * Interface used by input streams that wrap other input streams, so that the underlying input
 * stream can be retrieved.
 *
 */
public interface InputStreamWrapper {
	
    /**
     * @return
     * the underlying input stream wrapped by this class.
     */
    public InputStream getWrappedInputStream();
    
}
