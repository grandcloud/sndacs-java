package com.snda.storage.service.io;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Input stream wrapper that allows the underlying wrapped input stream to be interrupted.
 * Every time a blocking stream operation is invoked on this class, the interrupt flag is
 * checked first. If this flag is set, the underlying stream is closed and an IOException
 * "Input Stream Interrupted" is thrown.
 * <p>
 * <b>Note</b>: This hacky class does not really solve the problem of interrupting blocking
 * Java input streams, as it cannot unblock a blocked read operation. It really just serves
 * as a convenient way to interrupt streams before any potentially blocking operations.
 *
 */
public class InterruptableInputStream extends InputStream implements
		InputStreamWrapper {

	private static final Log log = LogFactory.getLog(InterruptableInputStream.class);

    private InputStream inputStream = null;

    private boolean interrupted = false;

    public InterruptableInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    private void maybeInterruptInputStream() throws IOException {
        if (interrupted) {
            if (log.isDebugEnabled()) {
                log.debug("Input stream interrupted, closing underlying input stream " +
                this.inputStream.getClass());
            }
            try {
                close();
            } catch (IOException ioe) {
                if (log.isWarnEnabled()) {
                    log.warn("Unable to close underlying InputStream on interrupt");
                }
            }
            // Throw an unrecoverable exception to indicate that this exception was deliberate, and
            // should not be recovered from.
            throw new UnrecoverableIOException("Reading from input stream deliberately interrupted");
        }
    }

    @Override
    public int read() throws IOException {
        maybeInterruptInputStream();
        return inputStream.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        maybeInterruptInputStream();
        return inputStream.read(b, off, len);
    }

    @Override
    public int available() throws IOException {
        maybeInterruptInputStream();
        return inputStream.available();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    public InputStream getWrappedInputStream() {
        return inputStream;
    }

    public void interrupt() {
        interrupted = true;
    }

}
