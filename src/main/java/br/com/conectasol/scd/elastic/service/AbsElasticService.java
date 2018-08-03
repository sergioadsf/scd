package br.com.conectasol.scd.elastic.service;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

import br.com.conectasol.scd.util.CloseUtil;

public abstract class AbsElasticService {

	protected RestHighLevelClient openConnection() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
	}

	protected void close(RestHighLevelClient client) {
		CloseUtil.close(client);
	}
}
