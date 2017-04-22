package com.ir.tennis.service;

import java.util.Map.Entry;

import com.ir.tennis.model.Query;

/**
 * @author giridar
 */
public interface QEScheme {
	public String getName();

	public Query expand(Query query);

	static class Tuple implements Entry<String, Float> {
		private String key;
		private Float value;

		public Tuple(String key, Float value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String getKey() {
			return key;
		}

		@Override
		public Float getValue() {
			return value;
		}

		@Override
		public Float setValue(Float value) {
			return null;
		}

		@Override
		public String toString() {
			return "Tuple [key=" + key + ", value=" + value + "]";
		}
	}
}
