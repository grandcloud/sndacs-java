package com.snda.storage.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

/**
 * 
 * @author wangzijian
 *
 */
public class MIMETypes {

	private static final Logger LOGGER = LoggerFactory.getLogger(MIMETypes.class);

	private static final String DEFAULT_MIMETYPE = "application/octet-stream";

	private final Map<String, String> map;
	
	private static class SingletonHolder {
		static MIMETypes instance = new MIMETypes();
	}
	
	private MIMETypes() {
		LOGGER.info("Loading mime types from file in the classpath: mime.types");
		try {
			map = loadMimetypes("/mime.types");
		} catch (IOException e) {
			throw new IllegalStateException("Failed to load mime types from file in the classpath: mime.types", e);
		}
	}

	public static MIMETypes getInstance() {
		return SingletonHolder.instance;
	}

	private static Map<String, String> loadMimetypes(String resource) throws IOException {
		Map<String, String> map = Maps.newHashMap();
		for (String line : readLines(resource)) {
			line = line.trim();
			if (line.startsWith("#") || line.length() == 0) {
				continue;
			} else {
				StringTokenizer st = new StringTokenizer(line, " \t");
				if (st.countTokens() > 1) {
					String mimetype = st.nextToken();
					while (st.hasMoreTokens()) {
						String extension = st.nextToken();
						map.put(extension, mimetype);
						if (LOGGER.isInfoEnabled()) {
							LOGGER.info("Setting mime type for extension '" + extension + "' to '" + mimetype + "'");
						}
					}
				} 
			}
		}
		return ImmutableMap.copyOf(map);
	}

	private static List<String> readLines(final String resource) throws IOException {
		return CharStreams.readLines(new InputSupplier<Reader>() {
			@Override
			public Reader getInput() throws IOException {
				return new InputStreamReader(MIMETypes.class.getResourceAsStream(resource));
			}
		});
	}

	public String getMimetype(String fileName) {
		int lastPeriod = fileName.lastIndexOf(".");
		if (lastPeriod > 0 && lastPeriod + 1 < fileName.length()) {
			String extension = fileName.substring(lastPeriod + 1);
			String mimetype = map.get(extension);
			if (mimetype != null) {
				LOGGER.info("Extension: '{}', mimetype: '{}'", extension, mimetype);
				return mimetype;
			}
		}
		return DEFAULT_MIMETYPE;
	}

	public String getMimetype(File file) {
		return getMimetype(file.getName());
	}
}
