package com.snda.storage.service.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SegmentedRepeatableFileInputStream extends
		RepeatableFileInputStream {
	private static final Log log = LogFactory.getLog(SegmentedRepeatableFileInputStream.class);

    protected long offset = 0;
    protected long segmentLength = 0;

    /**
     * Creates a repeatable input stream based on a file.
     *
     * @param file
     * @throws IOException
     */
    public SegmentedRepeatableFileInputStream(File file, long offset, long segmentLength)
        throws IOException
    {
        super(file);
        this.offset = offset;
        this.segmentLength = segmentLength;

        if (segmentLength < 1) {
            throw new IllegalArgumentException(
                "Segment length " + segmentLength + " must be greater than 0");
        }
        // Sanity check segment bounds against underlying file
        if (file.length() < this.offset + this.segmentLength) {
            throw new IllegalArgumentException(
                "Offset " + offset + " plus segment length " + segmentLength
                + "exceed length of file " + file);
        }

        // Skip forward to requested offset in file input stream.
        skipToOffset();
    }

    private void skipToOffset() throws IOException {
        long skipped = 0;
        long toSkip = offset;
        while (toSkip > 0) {
            skipped = skip(toSkip);
            toSkip -= skipped;
        }

        // Mark the offset location so we will return here on reset
        super.mark(0);

        if (log.isDebugEnabled()) {
            log.debug("Skipped to segment offset " + offset);
        }
    }

    @Override
    public int available() throws IOException {
        long reallyAvailable = this.segmentLength -
            (bytesReadPastMarkPoint + getRelativeMarkPoint());
        if (reallyAvailable > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) reallyAvailable;
    }

    @Override
    public int read() throws IOException {
        // Ensure we don't read beyond the segment length
        if (bytesReadPastMarkPoint + getRelativeMarkPoint() >= segmentLength) {
            return -1;
        } else {
            return super.read();
        }
    }

    @Override
    public int read(byte[] bytes, int off, int len) throws IOException {
        bytesReadPastMarkPoint += off;
        // Ensure we don't read beyond the segment length
        if (bytesReadPastMarkPoint + getRelativeMarkPoint() >= segmentLength) {
            return -1;
        }
        if (bytesReadPastMarkPoint + getRelativeMarkPoint() + len > segmentLength) {
            len = (int) (segmentLength - (bytesReadPastMarkPoint + getRelativeMarkPoint() + off));
        }
        return super.read(bytes, off, len);
    }

    private long getRelativeMarkPoint() {
        return markPoint - this.offset;
    }

}
