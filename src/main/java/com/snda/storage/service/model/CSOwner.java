package com.snda.storage.service.model;


/**
 * Represents an CS owner object with a canonical ID and, optionally, a display name.
 *
 */
public class CSOwner extends StorageOwner {
	
	public CSOwner() {
        super();
    }

    public CSOwner(String id, String displayName) {
        super(id, displayName);
    }
	
}
