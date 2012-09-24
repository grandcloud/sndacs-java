package com.snda.storage;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public enum Location {

	HUABEI_1("huabei-1"), 
	HUADONG_1("huadong-1");

	public static final Location DEFAULT = HUADONG_1;
	public static final Location PREFERRED = HUABEI_1;

	private final String name;
	private final String endpoint;
	private final String publicEndpoint;

	private Location(String name) {
		this.name = name;
		this.endpoint = "storage-" + name + ".grandcloud.cn";
		this.publicEndpoint = "storage-" + name + ".sdcloud.cn";
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getPublicEndpoint() {
		return publicEndpoint;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	public static Location parse(String text) {
		if (text == null) {
			return null;
		}
		for (Location location : Location.values()) {
			if (location.getName().equals(text)) {
				return location;
			}
		}
		throw new IllegalArgumentException(text);
	}
}
