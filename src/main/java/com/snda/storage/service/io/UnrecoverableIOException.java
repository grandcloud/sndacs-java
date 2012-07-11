package com.snda.storage.service.io;

import java.io.IOException;


/**
 * Indicates an IOException that cannot, or should not, be recovered from.
 *
 */
public class UnrecoverableIOException extends IOException {
	private static final long serialVersionUID = 1423979730178522822L;

    public UnrecoverableIOException(String message) {
        super(message);
    }
}
