package com.ir.tennis.model;

import org.apache.solr.common.SolrDocument;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Document {
	private String title;
	private String url;
	private String content;
	private float score;

	public Document(SolrDocument document) {
		this.title = (String) document.get("title");
		this.url = (String) document.get("url");
		this.content = (String) document.get("content");
		this.score = (float) document.get("score");
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "Document [title=" + title + ", url=" + url + ", content=" + content + ", score=" + score + "]";
	}
}
