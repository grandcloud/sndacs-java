package com.snda.storage.core.support;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class HttpDateTimeFormatter {

	public static final SimpleDateFormat RFC1123 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);

	static {
		RFC1123.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	private HttpDateTimeFormatter() {
	}

	public static DateTime parseDateTime(String string) {
		if (string == null) {
			return null;
		}
		try {
			return new DateTime(RFC1123.parse(string));
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static String formatDateTime(DateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return RFC1123.format(dateTime.toDate());
	}
}
