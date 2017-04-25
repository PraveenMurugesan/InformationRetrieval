package com.ir.tennis.model;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Query {
	private String query;
	private int start;
	private int rows;
	private String order;

	public Query() {
	}

	public Query(String query) {
		this.query = query;
	}

	public Query(String query, int start, int rows, String order) {
		this.query = query;
		this.start = start;
		this.rows = rows;
		this.order = order;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "Query [query=" + query + ", start=" + start + ", rows=" + rows + ", order=" + order + "]";
	}

	public Query clone() {
		return new Query(query, start, rows, order);
	}
}
