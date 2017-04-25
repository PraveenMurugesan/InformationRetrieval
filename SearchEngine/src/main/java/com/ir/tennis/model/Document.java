package com.ir.tennis.model;

import org.apache.solr.common.SolrDocument;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Document {
	private String title;
	private String url;
	private String content;
	private int kClusterId;
	private int aggClusterId1;
	private int aggClusterId2;

	public Document(SolrDocument document) {
		this.title = (String) document.get("title");
		this.url = (String) document.get("url");
		this.content = (String) document.get("content");
		this.kClusterId = document.get("kClusterId") != null ? (int) document.get("kClusterId") : 0;
		this.aggClusterId1 = document.get("aggClusterId1") != null ? (int) document.get("aggClusterId1") : 0;
		this.aggClusterId2 = document.get("aggClusterId1") != null ? (int) document.get("aggClusterId2") : 0;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getkClusterId() {
		return kClusterId;
	}

	public void setkClusterId(int kClusterId) {
		this.kClusterId = kClusterId;
	}

	public int getAggClusterId1() {
		return aggClusterId1;
	}

	public void setAggClusterId1(int aggClusterId1) {
		this.aggClusterId1 = aggClusterId1;
	}

	public int getAggClusterId2() {
		return aggClusterId2;
	}

	public void setAggClusterId2(int aggClusterId2) {
		this.aggClusterId2 = aggClusterId2;
	}

	@Override
	public String toString() {
		return "Document [title=" + title + ", url=" + url + ", content=" + content + ", kClusterId=" + kClusterId
				+ ", aggClusterId1=" + aggClusterId1 + ", aggClusterId2=" + aggClusterId2 + "]";
	}
}
