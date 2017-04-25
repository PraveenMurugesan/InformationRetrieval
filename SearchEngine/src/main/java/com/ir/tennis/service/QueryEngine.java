package com.ir.tennis.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ir.tennis.model.Document;
import com.ir.tennis.model.Query;
import com.ir.tennis.model.Result;

/**
 * Created by Srikanth on 4/19/2017.
 */
@Service
public class QueryEngine {
	private static final Logger logger = Logger.getLogger(QueryEngine.class);

	@Autowired
	SolrClient solrClient;

	public Result execute(Query query) {
		List<Document> documents = new ArrayList<>();
		Result result = new Result(query, 0, documents);
		try {
			logger.info("Querying Solr: " + query);
			SolrDocumentList queryDocuments = solrClient.query(_getSolrQuery(query)).getResults();
			logger.info("Solr document matches: " + queryDocuments.getNumFound());
			result.setMatches(queryDocuments.getNumFound());
			/* convert query documents to query result set */
			for (SolrDocument document : queryDocuments)
				documents.add(new Document(document));

		} catch (SolrServerException e) {
			logger.error(e);

		} catch (IOException e) {
			logger.error(e);

		}
		return result;
	}

	private SolrQuery _getSolrQuery(Query query) {
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(query.getQuery());
		solrQuery.setParam("fl", "title,url,content,kClusterId,aggClusterId1,aggClusterId2");
		solrQuery.setStart(query.getStart());
		if (query.getRows() > 0)
			solrQuery.setRows(query.getRows());
		solrQuery.addSort("score", ORDER.desc);
		if (query.getOrder() != null)
			solrQuery.addSort(query.getOrder(), ORDER.desc);
		return solrQuery;
	}
}
