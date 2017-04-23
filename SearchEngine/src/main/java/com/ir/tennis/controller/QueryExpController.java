package com.ir.tennis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;
import com.ir.tennis.service.QueryEngine;
import com.ir.tennis.service.QueryExpEngine;

/**
 * @author giridar
 */
@RestController
public class QueryExpController {
	@Autowired
	QueryExpEngine queryExpEngine;

	@Autowired
	QueryEngine queryEngine;

	@RequestMapping("/expand")
	public Result run(@RequestParam(value = "opt", defaultValue = "Rocchio") String opt, Query query) {
		return queryEngine.executeQuery(queryExpEngine.expand(query, opt));
	}
}
