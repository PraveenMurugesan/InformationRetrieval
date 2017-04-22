package com.ir.tennis.service;

/**
 * @author giridar
 */
public class QEConfig {
	int documentSetSize;
	int clusterSize;
	float alpha;
	float beta;

	public QEConfig(int documentSetSize, int clusterSize, float alpha, float beta) {
		this.documentSetSize = documentSetSize;
		this.clusterSize = clusterSize;
		this.alpha = alpha;
		this.beta = beta;
	}
}
