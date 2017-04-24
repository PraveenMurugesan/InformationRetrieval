package com.ir.tennis.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;

/**
 * @author giridar
 */
@Service
public class QueryExpEngine {
	private static final Logger logger = Logger.getLogger(QueryExpEngine.class);

	@Autowired
	QERocchioScheme rocchio;

	@Autowired
	QEAssociationCluster associationCluster;

	@Autowired
	QEMetricCluster metricCluster;

	@Autowired
	QueryEngine queryEngine;

	public Result expand(Query query, String opt) {
		QEScheme scheme = rocchio;
		if (associationCluster.getName().equals(opt))
			scheme = associationCluster;
		else if (metricCluster.getName().equals(opt))
			scheme = metricCluster;

		logger.info("Starting " + scheme.getName() + " for " + query);
		Query expandedQuery = scheme.expand(query);
		Result result = queryEngine.execute(expandedQuery);
		result.setExpandedQuery(expandedQuery.getQuery());
		return result;
	}
}
