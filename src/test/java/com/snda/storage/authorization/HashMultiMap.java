package com.snda.storage.authorization;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * 
 * @author wangzijian@snda.com
 * 
 * @param <K>
 * @param <V>
 */
public class HashMultiMap<K, V> extends ForwardingMap<K, List<V>> {

	private final Map<K, List<V>> map = Maps.newHashMap();

	public static <K, V> HashMultiMap<K, V> create() {
		return new HashMultiMap<K, V>();
	}

	public void add(K key, V value) {
		getList(key).add(value);
	}

	public void putSingle(K key, V value) {
		List<V> list = getList(key);
		list.clear();
		list.add(value);
	}

	private List<V> getList(K key) {
		List<V> list = map.get(key);
		if (list == null) {
			list = Lists.newArrayList();
			map.put(key, list);
		}
		return list;
	}

	@Override
	protected Map<K, List<V>> delegate() {
		return map;
	}

}