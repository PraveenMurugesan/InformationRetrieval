package com.ir.tennis.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Query;

/**
 * @author giridar
 */
@Service
public class QueryExpEngine {
	private static final Logger logger = Logger.getLogger(QueryExpEngine.class);

	@Autowired
	QEAssociationCluster associationCluster;

	public Query expand(final Query query, String opt) {
		QEScheme scheme = associationCluster;
		if (opt.equals(associationCluster.getName()))
			scheme = associationCluster;

		logger.info("Starting " + opt + " for " + query);
		return scheme.expand(query);
	}
}
