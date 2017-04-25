package com.ir.tennis.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Document;
import com.ir.tennis.model.Query;
import com.ir.tennis.util.Counter;

/**
 * @author giridar
 */
@Service
public class QERocchioScheme implements QEScheme {
	private static final Logger logger = Logger.getLogger(QERocchioScheme.class);
	String name = "Rocchio";

	@Autowired
	QueryEngine queryEngine;

	@Autowired
	DocumentProcessor documentProcessor;

	@Autowired
	QEConfig qeConfig;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Query expand(Query query) {
		/* Original query vector */
		Set<String> queryVector = new HashSet<>();
		for (String term : documentProcessor.getTokens(query.getQuery()))
			queryVector.add(term);

		/*
		 * Optimal query vector is formed by summing the relevant document
		 * vectors and subtracting the non-relevant document vectors
		 */
		Counter<String> optimalVector = new Counter<>(new HashMap<>());
		/* Fetch relevant documents */
		Query subQuery = query.clone();
		subQuery.setRows(qeConfig.documentSetSize);
		formOptimalVector(queryEngine.execute(subQuery).getDocuments(), optimalVector, queryVector, true);
		/* Fetch non-relevant documents */
		subQuery.setQuery("NOT(" + query.getQuery() + ")");
		formOptimalVector(queryEngine.execute(subQuery).getDocuments(), optimalVector, queryVector, false);

		/* Pick top K dimensions (doc terms) from the optimal query vector */
		StringJoiner expandedQueryBuilder = new StringJoiner(" ");
		expandedQueryBuilder.add(query.getQuery());
		List<Entry<String, Float>> expandedTerms = optimalVector.top(queryVector.size() * qeConfig.clusterSize);
		logger.info("New query terms: " + expandedTerms);
		for (Entry<String, Float> term : expandedTerms)
			expandedQueryBuilder.add(term.getKey());

		query.setQuery(expandedQueryBuilder.toString());
		return query;
	}

	private void formOptimalVector(List<Document> docs, Counter<String> optimalVector, Set<String> queryVector,
			boolean relevant) {
		for (Document doc : docs) {
			List<String> tokens = documentProcessor.getTokens(doc.getContent());
			logger.debug("Tokens of " + doc.getUrl() + ": " + tokens);
			for (String term : tokens) {
				if (!queryVector.contains(term)) {
					if (relevant)
						optimalVector.add(term);
					else
						optimalVector.subtract(term);
				}
			}
		}
	}
}
