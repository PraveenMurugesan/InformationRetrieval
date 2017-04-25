package com.ir.tennis;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * Created by Srikanth on 4/19/2017.
 */
public class Main {
	private static SolrClient solrClient;
	private static String rankFilePath;
	private static String hitsFilePath;

	public static void main(String[] args) {
		if (args.length < 3) {
			System.out.println("Not enough input arguments.");
			System.out.println(
					"Correct usage: 'gradle execute -PsolrUrl=<url> -PrankFilePath=<path1> -PhitsFilePath=<path2>'");
			return;
		}

		System.out.println("Arguments: " + Arrays.asList(args));
		solrClient = new HttpSolrClient.Builder(args[0]).build();
		rankFilePath = args[1];
		hitsFilePath = args[2];

		System.out.println("Processing Rank file");
		// process Rank file and load it to a hashMap
		HashMap<String, Float> rankMap = processRankFile();
		System.out.println("Finished processing Rank file");

		System.out.println("Constructing Web graph");
		// process hit file and load it to load map
		HashMap<String, Float> hitsMap = constructWebGraph();
		System.out.println("Finished Web graph construction");

		// find total count of documents in solr
		long documentCount = getDocumentCount();

		try {
			int startIndex = 0;
			int rowCount = 10000;

			SolrQuery solrQuery = null;
			SolrDocumentList queryDocuments = null;
			int i = 0;
			long startTime = System.currentTimeMillis();
			while (i < documentCount) {
				solrQuery = new SolrQuery();
				solrQuery.setQuery("*:*");
				solrQuery.setParam("fl",
						"id,title,url,content,tstamp,boost,segment,anchor,digest,rankScore,hitScore,kClusterId,aggClusterId1,aggClusterId2");
				solrQuery.addSort("score", SolrQuery.ORDER.desc);
				solrQuery.setStart(startIndex);
				solrQuery.setRows(rowCount);
				QueryResponse queryResponse = solrClient.query(solrQuery);
				queryDocuments = queryResponse.getResults();
				for (int j = 0; j < queryDocuments.size(); j++) {
					SolrDocument solrDocument = queryDocuments.get(j);
					SolrInputDocument newDoc = new SolrInputDocument();
					newDoc.addField("id", solrDocument.getFieldValue("id"));
					newDoc.addField("title", solrDocument.getFieldValue("title"));
					newDoc.addField("url", solrDocument.getFieldValue("url"));
					newDoc.addField("content", solrDocument.getFieldValue("content"));
					newDoc.addField("boost", solrDocument.getFieldValue("boost"));
					newDoc.addField("tstamp", solrDocument.getFieldValue("tstamp"));
					newDoc.addField("segment", solrDocument.getFieldValue("segment"));
					newDoc.addField("digest", solrDocument.getFieldValue("digest"));
					newDoc.addField("anchor", solrDocument.getFieldValue("anchor"));
					newDoc.addField("kClusterId", solrDocument.getFieldValue("kClusterId"));
					newDoc.addField("aggClusterId1", solrDocument.getFieldValue("aggClusterId1"));
					newDoc.addField("aggClusterId2", solrDocument.getFieldValue("aggClusterId2"));
					newDoc.addField("rankScore", rankMap.getOrDefault(solrDocument.getFieldValue("id"), 0.00f));
					newDoc.addField("hitScore", hitsMap.getOrDefault(solrDocument.getFieldValue("id"), 0.00f));
					solrClient.add(newDoc);
					i++;
				}
				System.out.println("Completed " + i + " number of documents");
				solrClient.commit();
				startIndex += rowCount;
				System.out.println("Start Index " + startIndex);
			}
			long endTime = System.currentTimeMillis();
			System.out.println("Time Taken to complete: " + (endTime - startTime) + " ms");

		} catch (SolrServerException sse) {
			sse.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long getDocumentCount() {
		long count = 0;
		try {
			SolrQuery solrQuery = new SolrQuery();
			solrQuery.setQuery("*:*");
			solrQuery.setParam("fl", "id,title,url,content");
			QueryResponse queryResponse = solrClient.query(solrQuery);
			SolrDocumentList queryDocuments = queryResponse.getResults();
			count = queryDocuments.getNumFound();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return count;
	}

	private static HashMap<String, Float> processRankFile() {
		HashMap<String, Float> rankMap = new HashMap<>();
		FileInputStream fileInputStream = null;
		DataInputStream dataInputStream = null;
		BufferedReader bufferedReader = null;
		try {
			File rankingFile = new File(rankFilePath);
			fileInputStream = new FileInputStream(rankingFile);
			dataInputStream = new DataInputStream(fileInputStream);
			bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				String[] split = line.split("\\s+");
				rankMap.put(split[0].trim(), Float.valueOf(split[1].trim()));
			}
		} catch (Exception e) {
			try {
				fileInputStream.close();
				dataInputStream.close();
				bufferedReader.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

		return rankMap;
	}

	public static HashMap<String, Float> constructWebGraph() {

		long noOfEdges = 0;
		long noOfNodes = 0;

		Graph<String, Long> webg = new DirectedSparseMultigraph<String, Long>();

		File f1 = new File(hitsFilePath);
		BufferedReader b1 = null;
		try {
			b1 = new BufferedReader(new FileReader(f1));
			String line = null;
			String[] url = new String[2];
			while ((line = b1.readLine()) != null) {

				String[] url1 = new String[6];

				if (line.contains("Inlinks:")) {
					url = line.split("Inlinks:");

					if (!webg.containsVertex(url[0].trim())) {
						webg.addVertex(url[0].trim());
						noOfNodes++;
					}

				} else if (line.contains("fromUrl:")) {
					url1 = line.split("\\s+");

					if (!webg.containsVertex(url1[2].trim())) {
						webg.addVertex(url1[2].trim());
						noOfNodes++;
					}
					noOfEdges++;
					webg.addEdge(noOfEdges, url1[2], url[0]);
				}
			}
			b1.close();
			return hitRanker(webg);
		} catch (FileNotFoundException e) {
			throw new RuntimeException("The file " + f1.getName() + " is not found....");
		} catch (IOException ioe) {
			throw new RuntimeException("IO Exception occurred!!!.");
		}

	}

	public static HashMap<String, Float> hitRanker(Graph<String, Long> webg)
			throws FileNotFoundException, UnsupportedEncodingException {
		HITS<String, Long> ranker = new HITS<String, Long>(webg, 0.15);
		ranker.initialize();
		ranker.setMaxIterations(10);
		ranker.evaluate();
		HashMap<String, Float> result = new HashMap<>();
		Iterator<String> i = webg.getVertices().iterator();

		while (i.hasNext()) {
			String v = i.next();
			result.put(v, Float.parseFloat(String.valueOf(ranker.getVertexScore(v).authority)));
		}

		return result;
	}

}
