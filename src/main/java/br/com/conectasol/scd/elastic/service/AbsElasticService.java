package br.com.conectasol.scd.elastic.service;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
//import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestHighLevelClient;

import br.com.conectasol.scd.util.CloseUtil;

public abstract class AbsElasticService {
	
	protected ThreadPoolExecutor executor;

	private RestClientBuilder createConnection() {
		return RestClient.builder(new HttpHost("localhost", 9200))
		        .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
		            @Override
		            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
		                return requestConfigBuilder.setConnectTimeout(5000)
		                        .setSocketTimeout(60000);
		            }
		        })
		        .setMaxRetryTimeoutMillis(60000);
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
}
