package com.ir.tennis.model;

import java.util.List;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Result {
	private Query query;
	private String expandedQuery;
	private long matches;
	private List<Document> documents;

	public Result(Query query, long matches, List<Document> documents) {
		this.query = query;
		this.matches = matches;
		this.documents = documents;
	}

	public Result(Query query, String expandedQuery, long matches, List<Document> documents) {
		this.query = query;
		this.expandedQuery = expandedQuery;
		this.matches = matches;
		this.documents = documents;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public String getExpandedQuery() {
		return expandedQuery;
	}

	public void setExpandedQuery(String expandedQuery) {
		this.expandedQuery = expandedQuery;
	}

	public long getMatches() {
		return matches;
	}

	public void setMatches(long matches) {
		this.matches = matches;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		return "Result [query=" + query + ", matches=" + matches + ", documents=" + documents + "]";
	}
}
