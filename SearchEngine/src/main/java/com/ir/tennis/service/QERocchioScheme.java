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
import com.ir.tennis.model.Result;
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
		/* Fetch documents for the actual query */
		int rows = query.getRows();
		query.setRows(qeConfig.documentSetSize);
		Result result = queryEngine.executeQuery(query);
		query.setRows(rows);

		/* Original query vector */
		Set<String> queryVector = new HashSet<>();
		for (String term : query.getQuery().split(" "))
			queryVector.add(term);

		/*
		 * Optimal query vector is formed by summing the individual document
		 * vectors and then the original query vector
		 */
		Counter<String> optimalVector = new Counter<>(new HashMap<>());
		for (Document doc : result.getDocuments()) {
			List<String> tokens = documentProcessor.getTokens(doc.getContent());
			logger.debug("Tokens of " + doc.getUrl() + ": " + tokens);
			for (String term : tokens)
				if (!queryVector.contains(term))
					optimalVector.add(term);
		}

		/* Pick top K dimensions (doc terms) from the optimal query vector */
		StringJoiner expandedQueryBuilder = new StringJoiner(" ");
		for (String term : queryVector)
			expandedQueryBuilder.add(term);
		List<Entry<String, Float>> expandedTerms = optimalVector.top(queryVector.size() * qeConfig.clusterSize);
		logger.info("New query terms: " + expandedTerms);
		for (Entry<String, Float> term : expandedTerms)
			expandedQueryBuilder.add(term.getKey());
		Query expandedQuery = query.clone();
		expandedQuery.setQuery(expandedQueryBuilder.toString());
		return expandedQuery;
	}
}
