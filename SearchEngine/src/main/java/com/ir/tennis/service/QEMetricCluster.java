package com.ir.tennis.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
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
public class QEMetricCluster implements QEScheme {
	private static final Logger logger = Logger.getLogger(QEMetricCluster.class);
	String name = "MetricCluster";

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

		/* Create Metric matrix for query terms */
		Map<String, Counter<String>> queryMetricVectors = new HashMap<>();
		for (String term : documentProcessor.getTokens(query.getQuery()))
			if (!queryMetricVectors.containsKey(term))
				queryMetricVectors.put(term, new Counter<>(new HashMap<>()));

		/* Calculate correlation values for all (query term, doc term) pairs */
		Map<String, List<Integer>> termIndices = new HashMap<>();
		for (Document doc : result.getDocuments()) {
			/* Record indices of all terms in the document */
			List<String> tokens = documentProcessor.getTokens(doc.getContent());
			logger.debug("Tokens of " + doc.getUrl() + ": " + tokens);
			int i = 0;
			for (String dTerm : tokens) {
				List<Integer> dTermIndices = termIndices.get(dTerm);
				if (dTermIndices == null) {
					dTermIndices = new LinkedList<>();
					termIndices.put(dTerm, dTermIndices);
				}
				dTermIndices.add(i++);
			}

			/* Calculate correlation values for this document */
			for (Entry<String, Counter<String>> queryMetricEntry : queryMetricVectors.entrySet()) {
				Counter<String> queryMetricVector = queryMetricEntry.getValue();
				List<Integer> qIndices = termIndices.get(queryMetricEntry.getKey());
				if (qIndices != null) {
					for (Entry<String, List<Integer>> docEntry : termIndices.entrySet()) {
						/* Calculate only for non-query terms */
						if (!queryMetricVectors.containsKey(docEntry.getKey())) {
							float metricCorrelation = 0;
							for (int dIndex : docEntry.getValue())
								for (int qIndex : qIndices)
									metricCorrelation += ((float) 1) / Math.abs(dIndex - qIndex);
							queryMetricVector.add(docEntry.getKey(), metricCorrelation);
						}
					}
				}
			}
			termIndices.clear();
		}

		/* Pick top K neighbors (doc terms) for every query term */
		StringJoiner expandedQueryBuilder = new StringJoiner(" ");
		expandedQueryBuilder.add(query.getQuery());
		Set<String> expandedTerms = new HashSet<>();
		for (Entry<String, Counter<String>> queryMetricEntry : queryMetricVectors.entrySet())
			for (Entry<String, Float> term : queryMetricEntry.getValue().top(qeConfig.clusterSize))
				expandedTerms.add(term.getKey());
		logger.info("New query terms: " + expandedTerms);
		for (String term : expandedTerms)
			expandedQueryBuilder.add(term);
		Query expandedQuery = query.clone();
		expandedQuery.setQuery(expandedQueryBuilder.toString());
		return expandedQuery;
	}
}
