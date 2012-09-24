package com.snda.storage;
import static com.google.common.base.Predicates.equalTo;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Maps.filterKeys;
import static com.google.common.collect.Maps.filterValues;

import java.util.Map;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.net.HttpHeaders;
import com.snda.storage.core.SNDAHeaders;
import com.snda.storage.core.support.Error;
import com.snda.storage.core.support.HttpDateTimeFormatter;
/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class SNDAServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final MapJoiner MESSAGE_JOINER = Joiner.on(", ").withKeyValueSeparator(": ");

	private static final String CODE = "Code";
	private static final String MESSAGE = "Message";
	private static final String RESOURCE = "Resource";

	private final int status;
	private final String message;
	private final Map<String, String> headers;
	private final Error error;

	public SNDAServiceException(int status, Map<String, String> headers, Error error) {
		this.status = status;
		this.headers = ImmutableMap.copyOf(headers);
		this.error = error == null ? Error.EMPTY : error;
		this.message = buildMessage();
	}

	private String buildMessage() {
		Map<String, String> message = Maps.newLinkedHashMap();
		message.put("Status", String.valueOf(getStatus()));
		message.put("Code", getCode());
		message.put("RequestId", getRequestId());
		message.put("Resource", getResource());
		message.putAll(filterKeys(error.getMap(), not(equalTo("RequestId"))));
		message.put("Message", getErrorMessage());
		return MESSAGE_JOINER.join(filterValues(message, notNull()));
	}

	@Override
	public String getMessage() {
		return message;
	}

	public DateTime getDate() {
		return HttpDateTimeFormatter.parseDateTime(headers.get(HttpHeaders.DATE));
	}

	public int getStatus() {
		return status;
	}

	public String getRequestId() {
		return headers.get(SNDAHeaders.REQUEST_ID);
	}

	public String getCode() {
		return error.get(CODE);
	}

	public String getResource() {
		return error.get(RESOURCE);
	}

	public String getErrorMessage() {
		return error.get(MESSAGE);
	}

	public Error getError() {
		return error;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

}
