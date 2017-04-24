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
			@RequestParam(value = "aggClusterId2", required = false) Integer aggClusterId2) {
		String query = "kClusterId:" + kClusterId;
		if (aggClusterId1 != null)
			query += " AND aggClusterId1:" + aggClusterId1;
		else if (aggClusterId2 != null)
			query += " AND aggClusterId2:" + aggClusterId2;

		return queryEngine.execute(new Query(query));
	}
}
