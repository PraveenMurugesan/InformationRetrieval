package com.ir.tennis.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.StringJoiner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Document;
import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;

/**
 * @author giridar
 */
@Service
public class QEAssociationCluster implements QEScheme {
	private static final Logger logger = Logger.getLogger(QEAssociationCluster.class);
	String name = "AssociatonCluster";

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
		/* Fetch results for the actual query */
		int rows = query.getRows();
		query.setRows(qeConfig.documentSetSize);
		Result result = queryEngine.executeQuery(query);
		query.setRows(rows);
		int n = result.getDocuments().size();

		/* Association matrix for query terms */
		Map<String, int[]> queryAssocVectors = new HashMap<>();
		for (String term : documentProcessor.getTokens(query.getQuery()))
			if (!queryAssocVectors.containsKey(term))
				queryAssocVectors.put(term, new int[n]);

		/* Association matrix for document terms */
		int i = 0;
		Map<String, int[]> localAssocVectors = new HashMap<>();
		for (Document doc : result.getDocuments()) {
			List<String> tokens = documentProcessor.getTokens(doc.getContent());
			logger.info(tokens);
			for (String term : tokens) {
				int[] Ft = queryAssocVectors.get(term);
				if (Ft == null) {
					Ft = localAssocVectors.get(term);
					if (Ft == null) {
						Ft = new int[n];
						localAssocVectors.put(term, Ft);
					}
				}
				Ft[i] += 1;
			}
			i++;
		}

		/* Cluster by top Correlation values */
		PriorityQueue<Entry<String, Float>> pq = new PriorityQueue<>(qeConfig.clusterSize,
				(e1, e2) -> e1.getValue().compareTo(e2.getValue()));
		Map<String, Integer> selfCorrelation = new HashMap<>();
		Set<String> expandedTerms = new HashSet<>();
		for (Entry<String, int[]> u : queryAssocVectors.entrySet()) {
			String Su = u.getKey();
			int[] Fu = u.getValue();
			int Cuu = correlation(Fu, Fu);
			for (Entry<String, int[]> v : localAssocVectors.entrySet()) {
				String Sv = v.getKey();
				int[] Fv = v.getValue();
				int Cvv = selfCorrelation.getOrDefault(Sv, -1);
				if (Cvv == -1) {
					Cvv = correlation(Fv, Fv);
					selfCorrelation.put(Sv, Cvv);
				}
				int Cuv = correlation(Fu, Fv);
				float Suv = ((float) Cuv) / (Cuu + Cvv + Cuv); // Normalize

				logger.info("Correlation(" + Su + ", " + Sv + ") = " + Suv);
				logger.info("Correlation values: " + Cuu + ", " + Cvv + ", " + Cuv);
				if (pq.size() < qeConfig.clusterSize) {
					pq.offer(new Tuple(Sv, Suv));
				} else if (Cuv > pq.peek().getValue()) {
					pq.poll();
					pq.offer(new Tuple(Sv, Suv));
				}
			}

			/* Top neighbors in the cluster */
			logger.info("Top neighbors: " + pq);
			while (!pq.isEmpty())
				expandedTerms.add(pq.poll().getKey());
		}

		/* Build the expanded query */
		logger.info("New terms added: " + expandedTerms);
		StringJoiner expandedQueryBuilder = new StringJoiner(" ");
		for (String term : queryAssocVectors.keySet())
			expandedQueryBuilder.add(term);
		for (String term : expandedTerms)
			expandedQueryBuilder.add(term);
		Query expandedQuery = query.clone();
		expandedQuery.setQuery(expandedQueryBuilder.toString());
		return expandedQuery;
	}

	private int correlation(int[] Fu, int[] Fv) {
		int Cuv = 0;
		int n = Fu.length;
		for (int i = 0; i < n; i++)
			Cuv += Fu[i] + Fv[i];
		return Cuv;
	}
}
