package com.ir.tennis.util;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Counter data structure similar to Python's collections.Counter
 * 
 * @author giridar
 */
public class Counter<K> implements Map<K, Integer> {
	private Map<K, Integer> map;

	public Counter(Map<K, Integer> map) {
		this.map = map;
	}

	public void add(K key) {
		map.merge(key, 1, Integer::sum);
	}

	public void add(K key, int count) {
		map.merge(key, count, Integer::sum);
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, Integer>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Integer get(Object key) {
		return map.get(key);
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Integer put(K key, Integer value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Integer> m) {
		map.putAll(m);
	}

	@Override
	public Integer remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Integer> values() {
		return map.values();
	}
}
