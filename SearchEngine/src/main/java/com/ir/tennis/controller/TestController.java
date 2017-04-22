package com.ir.tennis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;
import com.ir.tennis.service.QueryEngine;

/**
 * TDOD: Remove after testing
 * 
 * @author giridar
 */
@RestController
public class TestController {
	@Autowired
	QueryEngine queryEngine;

	@RequestMapping("/test")
	public Result run(@RequestParam(value = "query", defaultValue = "*.*") String query) {
		return queryEngine.executeQuery(new Query(query));
	}
}