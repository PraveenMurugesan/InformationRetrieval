package com.ir.tennis.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Counter data structure similar to Python's collections.Counter
 * 
 * @author giridar
 */
public class Counter<K> implements Map<K, Float> {
	private Map<K, Float> map;

	public Counter(Map<K, Float> map) {
		this.map = map;
	}

	public void add(K key) {
		map.merge(key, 1f, Float::sum);
	}

	public void add(K key, float count) {
		map.merge(key, count, Float::sum);
	}

	public void subtract(K key) {
		map.merge(key, -1f, Float::sum);
	}

	public void subtract(K key, float count) {
		map.merge(key, -count, Float::sum);
	}

	public void addAll(Collection<K> c) {
		for (K key : c)
			map.merge(key, 1f, Float::sum);
	}

	public List<Entry<K, Float>> top(int k) {
		PriorityQueue<Entry<K, Float>> pq = new PriorityQueue<>(k, (e1, e2) -> e1.getValue().compareTo(e2.getValue()));
		for (Entry<K, Float> entry : entrySet()) {
			if (pq.size() < k) {
				pq.offer(entry);
			} else if (entry.getValue() > pq.peek().getValue()) {
				pq.poll();
				pq.offer(entry);
			}
		}

		List<Entry<K, Float>> topEntries = new ArrayList<Entry<K, Float>>(k);
		while (!pq.isEmpty())
			topEntries.add(pq.poll());
		return topEntries;
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
	public Set<java.util.Map.Entry<K, Float>> entrySet() {
		return map.entrySet();
	}

	@Override
	public Float get(Object key) {
		return map.getOrDefault(key, 0f);
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
	public Float put(K key, Float value) {
		return map.put(key, value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends Float> m) {
		map.putAll(m);
	}

	@Override
	public Float remove(Object key) {
		return map.remove(key);
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<Float> values() {
		return map.values();
	}

	@Override
	public String toString() {
		return "Counter [map=" + map + "]";
	}
}
