package br.com.conectasol.scd.elastic.service;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClient;

import br.com.conectasol.scd.util.CloseUtil;

public abstract class AbsElasticService {
	
	protected ThreadPoolExecutor executor;

	protected RestClient openConnection2() {
		return RestClient.builder(
		        new HttpHost("localhost", 9200, "http")).build();
	}
	
	protected RestHighLevelClient openConnection() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
	}

	protected void close(RestClient client) {
		CloseUtil.close(client);
	}

	protected void close(RestHighLevelClient client) {
		CloseUtil.close(client);
	}
}
