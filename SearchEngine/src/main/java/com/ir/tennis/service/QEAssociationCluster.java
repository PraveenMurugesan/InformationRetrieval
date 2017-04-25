package com.ir.tennis.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
public class QEAssociationCluster implements QEScheme {
	private static final Logger logger = Logger.getLogger(QEAssociationCluster.class);
	String name = "AssociationCluster";

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
		Result result = queryEngine.execute(query);
		query.setRows(rows);

		/* Create Association matrix for query terms */
		Map<String, Counter<String>> queryAssocVectors = new HashMap<>();
		for (String term : documentProcessor.getTokens(query.getQuery()))
			if (!queryAssocVectors.containsKey(term))
				queryAssocVectors.put(term, new Counter<>(new HashMap<>()));

		/* Calculate correlation values for all (query term, doc term) pairs */
		Counter<String> docVector = new Counter<>(new HashMap<>());
		for (Document doc : result.getDocuments()) {
			/* Count frequencies of all terms in the document */
			List<String> tokens = documentProcessor.getTokens(doc.getContent());
			logger.debug("Tokens of " + doc.getUrl() + ": " + tokens);
			docVector.addAll(tokens);

			/* Calculate correlation values for this document */
			for (Entry<String, Counter<String>> queryAssocEntry : queryAssocVectors.entrySet()) {
				Counter<String> queryAssocVector = queryAssocEntry.getValue();
				Float qCount = docVector.get(queryAssocEntry.getKey());
				if (qCount != 0) {
					for (Entry<String, Float> docEntry : docVector.entrySet())
						queryAssocVector.add(docEntry.getKey(), docEntry.getValue() * qCount);
				}
			}
			docVector.clear();
		}

		/* Pick top K neighbors (doc terms) for every query term */
		StringJoiner expandedQueryBuilder = new StringJoiner(" ");
		expandedQueryBuilder.add(query.getQuery());
		Set<String> expandedTerms = new HashSet<>();
		for (Entry<String, Counter<String>> queryAssocEntry : queryAssocVectors.entrySet()) {
			int i = 0;
			for (Entry<String, Float> term : queryAssocEntry.getValue()
					.top(qeConfig.clusterSize + queryAssocVectors.keySet().size())) {
				if (!queryAssocVectors.containsKey(term.getKey())) {
					expandedTerms.add(term.getKey());
					i++;
				}
				if (i == qeConfig.clusterSize)
					break;
			}
		}
		logger.info("New query terms: " + expandedTerms);
		for (String term : expandedTerms)
			expandedQueryBuilder.add(term);
		Query expandedQuery = query.clone();
		expandedQuery.setQuery(expandedQueryBuilder.toString());
		return expandedQuery;
	}
}
