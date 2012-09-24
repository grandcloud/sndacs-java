package com.snda.storage.authorization;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.net.HttpHeaders;
import com.snda.storage.core.SNDAHeaders;
/**
 * 
 * @author wangzijian@snda.com
 *
 */
public class Canonicalization {

	private static final char NEW_LINE = (char) 0x000A;
	private static final String EMPTY_STRING = "";
	private static final Joiner NEW_LINE_JOINER = Joiner.on(NEW_LINE).useForNull(EMPTY_STRING);
	private static final MapJoiner NEW_LINE_MAP_JOINER = NEW_LINE_JOINER.withKeyValueSeparator(":");
	private static final Joiner EXTEND_VALUE_JOINER = Joiner.on(",");
	private static final String PARAMETER_ENTRY_SEPARATOR = "&";
	private static final String PARAMETER_KEY_VALUE_SEPARATOR = "=";
	private static final String FORWARD_SLASH = "/";
	private static final String QUESTION_MARK = "?";
	private static final String EXPIRES = "Expires";
	
	private static final List<String> RESPONSE_OVERRIDING_PARAMETERS = ImmutableList.of(
			"response-content-type",
			"response-content-language",
			"response-expires",
			"response-cache-control",
			"response-content-disposition",
			"response-content-encoding");
	private static final List<String> SUB_RESOURCES = ImmutableList.of(
			"lifecycle",
			"location",
			"logging",
			"partNumber",
			"policy",
			"uploadId",
			"uploads",
			"versionId",
			"versioning",
			"versions",
			"website");
	
	private Canonicalization() {
	}
	
	public static String canonicalize(CanonicalizableRequest request) {
		checkNotNull(request);
		List<String> elements = Lists.newArrayList(
				httpVerb(request),
				contentMD5(request),
				contentType(request),
				date(request));
		String canonicalizedSNDAHeaders = getCanonicalizedSNDAHeaders(request.getHeaders());
		if (StringUtils.isNotBlank(canonicalizedSNDAHeaders)) {
			elements.add(canonicalizedSNDAHeaders);
		}
		elements.add(getCanonicalizedResource(request));
		return NEW_LINE_JOINER.join(elements);
	}

	private static String contentType(CanonicalizableRequest request) {
		return first(request.getHeaders(), HttpHeaders.CONTENT_TYPE);
	}

	private static String contentMD5(CanonicalizableRequest request) {
		return first(request.getHeaders(), HttpHeaders.CONTENT_MD5);
	}

	private static String httpVerb(CanonicalizableRequest request) {
		return request.getMethod();
	}

	private static String getCanonicalizedResource(CanonicalizableRequest request) {
		StringBuilder canonicalizedResource = new StringBuilder();
		canonicalizedResource.append(getUndecodedRequestURI(request));
		if (canonicalizedResource.length() == 0) {
			canonicalizedResource.append(FORWARD_SLASH);
		}
		String subResourceAndParameters = getSubResourceAndParameters(request.getParameters());
		if (StringUtils.isNotBlank(subResourceAndParameters)) {
			canonicalizedResource.append(QUESTION_MARK).append(subResourceAndParameters);
		}
		return canonicalizedResource.toString();
	}

	private static String getSubResourceAndParameters(Map<String, List<String>> parameters) {
		SortedMap<String, String> map = Maps.newTreeMap(Ordering.natural());
		map.putAll(getSubResources(parameters));
		map.putAll(getResponseOverridingParameters(parameters));
		return joinMap(map);
	}

	private static String getUndecodedRequestURI(CanonicalizableRequest request) {
		String undecodedPath = request.getUndecodedPath();
		if (StringUtils.isBlank(undecodedPath)) {
			return EMPTY_STRING;
		}
		if (!undecodedPath.startsWith(FORWARD_SLASH)) {
			undecodedPath += FORWARD_SLASH;
		}
		return undecodedPath;
	}

	private static String getCanonicalizedSNDAHeaders(Map<String, List<String>> headers) {
		Map<String, String> map = Maps.newTreeMap(Ordering.natural());
		for (Entry<String, List<String>> each : headers.entrySet()) {
			String key = each.getKey();
			String lowerCasedKey = key.toLowerCase();
			if (lowerCasedKey.startsWith(SNDAHeaders.SNDA_PREFIX)) {
				String value = EXTEND_VALUE_JOINER.join(valueToTrimmedString(each.getValue()));
				map.put(lowerCasedKey, value);
			}
		}
		return NEW_LINE_MAP_JOINER.join(map);
	}
	
	private static Map<String, String> getResponseOverridingParameters(Map<String, List<String>> parameters) {
		Map<String, String> responseOverridingParameters = Maps.newHashMap();
		for (String name : RESPONSE_OVERRIDING_PARAMETERS) {
			String value = first(parameters, name);
			if (StringUtils.isNotBlank(value)) {
				responseOverridingParameters.put(name, value);
			}
		}
		return responseOverridingParameters;
	}
	
	private static Map<String, String> getSubResources(Map<String, List<String>> parameters) {
		Map<String, String> subRespurces = Maps.newHashMap();
		for (String subResource : SUB_RESOURCES) {
			if (parameters.containsKey(subResource)) {
				String value = first(parameters, subResource);
				subRespurces.put(subResource, value);
			}
		}
		return subRespurces;
	}
	
	private static String joinMap(Map<String, String> subResources) {
		StringBuilder sb = new StringBuilder();
		Iterator<? extends Map.Entry<String, String>> iterator = subResources.entrySet().iterator();
		if (iterator.hasNext()) {
			Entry<String, String> entry = iterator.next();
			sb.append(toString(entry));
			while (iterator.hasNext()) {
				Entry<String, String> e = iterator.next();
				sb.append(PARAMETER_ENTRY_SEPARATOR);
				sb.append(toString(e));
			}
		}
		return sb.toString();
	}
	
	private static String toString(Entry<String, String> entry) {
		String key = entry.getKey();
		String value = entry.getValue();

		StringBuilder sb = new StringBuilder();
		sb.append(key);
		if (StringUtils.isNotBlank(value)) {
			sb.append(PARAMETER_KEY_VALUE_SEPARATOR);
			sb.append(value);
		}
		return sb.toString();
	}
	
	private static String date(CanonicalizableRequest request) {
		String expires = first(request.getParameters(), EXPIRES);
		if (StringUtils.isNotBlank(expires)) {
			return expires;
		}
		if (request.getHeaders().containsKey(SNDAHeaders.DATE)) {
			return EMPTY_STRING;
		}
		String date = first(request.getHeaders(), HttpHeaders.DATE);
		if (StringUtils.isBlank(date)) {
			throw new IllegalStateException("Missing request timestamp");
		}
		return date;
	}
	
	private static <T> T first(Map<String, List<T>> map, String key) {
		List<T> values = map.get(key);
		if (values != null && values.size() > 0) {
			return values.get(0);
		}
		return null;
	}
	
	private static List<String> valueToTrimmedString(List<?> list) {
		return Lists.transform(list, new Function<Object, String>() {
			@Override
			public String apply(Object input) {
				return input.toString().trim();
			}
		});
	}
}
