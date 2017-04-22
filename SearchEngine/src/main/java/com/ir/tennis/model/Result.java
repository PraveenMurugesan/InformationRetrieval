package com.ir.tennis.model;

import java.util.List;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Result {
	private Query query;
	private long matches;
	private List<Document> documents;

	public Result(Query query, long matches, List<Document> documents) {
		this.query = query;
		this.matches = matches;
		this.documents = documents;
	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public long getMatches() {
		return matches;
	}

	public void setMatches(long matches) {
		this.matches = matches;
	}

	@Override
	public String toString() {
		return "Result [query=" + query + ", matches=" + matches + ", documents=" + documents + "]";
	}
}
