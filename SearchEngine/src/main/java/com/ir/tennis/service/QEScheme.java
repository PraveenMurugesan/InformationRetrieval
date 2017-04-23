package com.ir.tennis.service;

import com.ir.tennis.model.Query;

/**
 * @author giridar
 */
public interface QEScheme {
	public String getName();

	public Query expand(Query query);
}
