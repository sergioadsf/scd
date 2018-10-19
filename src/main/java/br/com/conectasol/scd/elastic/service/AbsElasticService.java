package br.com.conectasol.scd.elastic.service;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClient;

import br.com.conectasol.scd.util.CloseUtil;

public abstract class AbsElasticService {

	private static final int TIMEOUT = 60000;
	protected ThreadPoolExecutor executor;

	private RestClientBuilder createConnection() {
		return RestClient.builder(new HttpHost("localhost", 9200))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT);
					}
				}).setMaxRetryTimeoutMillis(TIMEOUT);
	}

	protected RestClient openConnection() {
		return this.createConnection().build();
	}

	protected RestHighLevelClient openHighConnection() {
		return new RestHighLevelClient(this.createConnection());
	}

	protected void close(RestClient client) {
		CloseUtil.close(client);
	}

	protected void close(RestHighLevelClient client) {
		CloseUtil.close(client);
	}

	protected String doGet(final RestClient client, StringBuilder jBuilder, String index) throws IOException {
		HttpEntity entity = new NStringEntity(jBuilder.toString(), ContentType.APPLICATION_JSON);
//		request.setEntity(entity);
		Response response = client.performRequest("GET", String.format("%s/%s", index, "_search"), Collections.emptyMap(), entity);
		return EntityUtils.toString(response.getEntity());
	}

	protected void doBulk(final RestClient client, StringBuffer jBuilder) throws IOException {
		this.doBulk(client, jBuilder.toString());
	}

	protected void doBulk(final RestClient client, StringBuilder jBuilder) throws IOException {
		this.doBulk(client, jBuilder.toString());
	}

	protected void doBulk(final RestClient client, String jBuilder) throws IOException {
//		Request request = new Request("POST", "_bulk", );
		HttpEntity entity = new NStringEntity(jBuilder.toString(), ContentType.APPLICATION_JSON);
//		request.setEntity(entity);
		Response response = client.performRequest("POST", "_bulk", Collections.emptyMap(), entity);
	}
}
