package com.ir.tennis.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;
import com.ir.tennis.service.QEAssociationCluster;
import com.ir.tennis.service.QEMetricCluster;
import com.ir.tennis.service.QERocchioScheme;
import com.ir.tennis.service.QEScheme;
import com.ir.tennis.service.QueryEngine;

/**
 * @author giridar
 */
@RestController
public class QueryController {
	private static final Logger logger = Logger.getLogger(QueryController.class);

	@Autowired
	QERocchioScheme rocchio;

	@Autowired
	QEAssociationCluster associationCluster;

	@Autowired
	QEMetricCluster metricCluster;

	@Autowired
	QueryEngine queryEngine;

	@RequestMapping("/query")
	public Result run(@RequestParam(value = "expand", required = false) String expand, Query query) {
		if (expand == null)
			return queryEngine.execute(query);

		QEScheme scheme = rocchio;
		if (associationCluster.getName().equals(expand))
			scheme = associationCluster;
		else if (metricCluster.getName().equals(expand))
			scheme = metricCluster;

		logger.info("Starting " + scheme.getName() + " for " + query);
		query = scheme.expand(query);

		Result result = queryEngine.execute(query);
		result.setExpandedQuery(query.getQuery());
		return result;
	}
}
