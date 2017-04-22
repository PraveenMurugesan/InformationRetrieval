package com.ir.tennis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;
import com.ir.tennis.service.QueryEngine;

/**
 * @author giridar
 */
@RestController
public class QueryController {
	@Autowired
	QueryEngine queryEngine;

	@RequestMapping("/query")
	public Result run(Query query) {
		return queryEngine.executeQuery(query);
	}
}