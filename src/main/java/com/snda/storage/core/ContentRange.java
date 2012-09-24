package com.snda.storage.core;

import static com.google.common.base.Preconditions.*;
import java.util.regex.Pattern;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class ContentRange extends ValueObject {

	private static final Pattern PATTERN = Pattern.compile("bytes \\d+-\\d+/\\d+");
	private static final String PREFIX = "bytes ";

	private final long firstBytePosition;
	private final long lastBytePosition;
	private final long instanceLength;

	public ContentRange(long firstBytePosition, long lastBytePosition, long instanceLength) {
		this.firstBytePosition = firstBytePosition;
		this.lastBytePosition = lastBytePosition;
		this.instanceLength = instanceLength;
	}

	public static ContentRange parseContentRange(String string) {
		if (string == null) {
			return null;
		}
		checkPattern(string);
		string = string.substring(PREFIX.length());
		int minus = string.indexOf("-");
		int slash = string.indexOf("/");
		long firstBytePosition = Long.valueOf(string.substring(0, minus));
		long lastBytePosition = Long.valueOf(string.substring(minus + 1, slash));
		long instanceLength = Long.valueOf(string.substring(slash + 1));
		return new ContentRange(firstBytePosition, lastBytePosition, instanceLength);
	}

	private static void checkPattern(String string) {
		checkArgument(PATTERN.matcher(string).find(), "Illegal Content-Range:%s ", string);
	}

	public long getFirstBytePosition() {
		return firstBytePosition;
	}

	public long getLastBytePosition() {
		return lastBytePosition;
	}

	public long getInstanceLength() {
		return instanceLength;
	}

}
