package br.com.conectasol.scd.elastic.service;

import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import br.com.conectasol.scd.util.CloseUtil;

public class ConsultarDoc {

	public static void main(String[] args) {
		RestHighLevelClient client = new RestHighLevelClient(
				RestClient.builder(new HttpHost("localhost", 9200, "http")));

		try {

			SearchRequest searchRequest = new SearchRequest("proposta");
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(QueryBuilders.matchQuery("message", "joaria"));
			searchRequest.source(searchSourceBuilder);
			
			
			HighlightBuilder highlightBuilder = new HighlightBuilder(); 
			HighlightBuilder.Field highlightTitle =
			        new HighlightBuilder.Field("name"); 
			highlightTitle.highlighterType("unified");  
			highlightBuilder.field(highlightTitle);  
			HighlightBuilder.Field highlightUser = new HighlightBuilder.Field("message");
			highlightBuilder.field(highlightUser);
			searchSourceBuilder.highlighter(highlightBuilder);
			
			
			SearchResponse searchResponse = client.search(searchRequest);
			RestStatus status = searchResponse.status();
			TimeValue took = searchResponse.getTook();
			Boolean terminatedEarly = searchResponse.isTerminatedEarly();
			boolean timedOut = searchResponse.isTimedOut();
			SearchHits hits = searchResponse.getHits();
			long totalHits = hits.getTotalHits();
			System.out.println("Total doc: "+totalHits);
			float maxScore = hits.getMaxScore();
			SearchHit[] searchHits = hits.getHits();
			
			for (SearchHit hit : searchHits) {
				String index = hit.getIndex();
				String type = hit.getType();
				String id = hit.getId();
				float score = hit.getScore();
				Map<String, Object> sourceAsMap = hit.getSourceAsMap();
				String name = (String) sourceAsMap.get("name");
				System.out.println(name);
				
				Map<String, HighlightField> highlightFields = hit.getHighlightFields();
				HighlightField highlight = highlightFields.get("message"); 
			    Text[] fragments = highlight.fragments();  
//			    String fragmentString = fragments[0].string();
//			    System.out.println(fragmentString);
//			    System.out.println("----------------------------------");
//			    System.out.println("----------------------------------");
			    System.out.println("");
			    for (Text t : fragments) {
			    	System.out.println(t);
			    }
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			CloseUtil.close(client);
		}
	}
}
