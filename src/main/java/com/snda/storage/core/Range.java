package com.snda.storage.core;




/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Range extends ValueObject {

	public static final String BYTES_UNIT = "bytes=";
	
	private final Long firstBytePosition;
	private final Long lastBytePosition;

	public Range(long firstBytePosition) {
		this.firstBytePosition = firstBytePosition;
		this.lastBytePosition = null;
	}

	public Range(long firstBytePosition, long lastBytePosition) {
		this.firstBytePosition = firstBytePosition;
		this.lastBytePosition = lastBytePosition;
	}

	@Override
	public String toString() {
		StringBuilder header = new StringBuilder().
				append(BYTES_UNIT).
				append(firstBytePosition).
				append("-");
		if (lastBytePosition != null) {
			header.append(lastBytePosition);
		}
		return header.toString();
	}

	public Long getFirstBytePosition() {
		return firstBytePosition;
	}

	public Long getLastBytePosition() {
		return lastBytePosition;
	}

}
