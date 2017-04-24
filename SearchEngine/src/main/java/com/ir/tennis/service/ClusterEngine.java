package com.ir.tennis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;

/**
 * @author giridar
 */
@Service
public class ClusterEngine {
	@Autowired
	QueryEngine queryEngine;

	public Result cluster(Integer kClusterId, Integer aggClusterId1, Integer aggClusterId2) {
		String query = "kClusterId:" + kClusterId;
		if (aggClusterId1 != null)
			query += " AND aggClusterId1:" + aggClusterId1;
		else if (aggClusterId2 != null)
			query += " AND aggClusterId2:" + aggClusterId2;

		return queryEngine.execute(new Query(query));
	}

}
