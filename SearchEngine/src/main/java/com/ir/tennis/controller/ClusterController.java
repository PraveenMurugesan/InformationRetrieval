package com.ir.tennis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;
import com.ir.tennis.service.QueryEngine;

/**
 * @author giridar
 */
@RestController
public class ClusterController {
	@Autowired
	QueryEngine queryEngine;

	@RequestMapping("/cluster")
	public Result run(@RequestParam(value = "kClusterId") Integer kClusterId,
			@RequestParam(value = "aggClusterId1", required = false) Integer aggClusterId1,
			@RequestParam(value = "aggClusterId2", required = false) Integer aggClusterId2, Query query) {
		String clusterQuery = "kClusterId:" + kClusterId;
		if (aggClusterId1 != null)
			clusterQuery += " AND aggClusterId1:" + aggClusterId1;
		else if (aggClusterId2 != null)
			clusterQuery += " AND aggClusterId2:" + aggClusterId2;

		query.setQuery(query.getQuery() + " AND " + clusterQuery);
		return queryEngine.execute(query);
	}
}
