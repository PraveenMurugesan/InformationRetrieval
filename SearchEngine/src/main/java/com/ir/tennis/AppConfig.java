package com.ir.tennis;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.ir.tennis.service.QEConfig;

/**
 * @author giridar
 *
 */
@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {
	@Bean
	SolrClient solrClient(@Value("${solr_url}") String solr_url) {
		return new HttpSolrClient.Builder(solr_url).build();
	}

	@Bean
	Set<String> stopWords(@Value("${stopwords}") String stopwords) {
		HashSet<String> stopWords = new HashSet<>();
		StringTokenizer st = new StringTokenizer(stopwords, ",");
		while (st.hasMoreTokens())
			stopWords.add(st.nextToken());
		return stopWords;
	}

	@Bean
	QEConfig qeConfig(@Value("${qe_config.document_set_size}") int documentSetSize,
			@Value("${qe_config.cluster_size}") int clusterSize, @Value("${qe_config.rocchio.alpha}") float alpha,
			@Value("${qe_config.rocchio.beta}") float beta) {
		return new QEConfig(documentSetSize, clusterSize, alpha, beta);
	}
}
