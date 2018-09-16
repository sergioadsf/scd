package br.com.conectasol.scd.elastic.service.folha;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;
import br.com.conectasol.scd.elastic.mapping.Folha;
import br.com.conectasol.scd.elastic.service.AbsElasticService;
import br.com.conectasol.scd.elastic.service.BulkBuilder;
import br.com.conectasol.scd.util.PathProperties;

@Service
public class FolhaService extends AbsElasticService {

	@Autowired
	private PathProperties prop;
	
	private static AtomicInteger at = new AtomicInteger(0);
	
	@PostConstruct
	public void init() {
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Folha> read(String anomes) throws IOException {
		String p = String.format("%sFolhaPagamento_%s.csv", prop.getCsv(), anomes);

		try (Reader reader = Files.newBufferedReader(Paths.get(p));) {

			return new CsvToBeanBuilder(reader).withType(Folha.class).withSeparator(';')
					.withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSkipLines(0).build().parse();
		}
	}

//	public void indexar(String anomes) {
//		try {
//			final RestHighLevelClient client = this.openConnection();
//
//			final BulkRequest request = new BulkRequest();
//			request.timeout(TimeValue.timeValueMinutes(4));
//			read(anomes).stream().forEach(folha -> {
//				try {
//					MIndex mIndex = folha.getClass().getAnnotation(MIndex.class);
//					String name = mIndex.name();
//					request.add(new IndexRequest(name, name, UUID.randomUUID().toString()).source("{\"ano_mes\": \" teste\"}", XContentType.JSON)).setRefreshPolicy(RefreshPolicy.WAIT_UNTIL);
////					request.add(new IndexRequest(name, name, UUID.randomUUID().toString())
////							.source(XContentFactory.jsonBuilder().startObject().field("ano_mes", "201807")
////									.field("nome_cargo_secundario", "Não se Aplica").endObject()));
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			});
//
//			client.bulk(request, RequestOptions.DEFAULT);
//			this.close(client);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	public void indexar(String anomes) {
		try {
			final RestClient client = this.openConnection2();
			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			String name = mIndex.name();
			
			StringBuilder jBuilder = new StringBuilder();
//			read(anomes).stream().forEach(folha -> {
//				try {
//					jBuilder.append(
//							BulkBuilder
//								.init(name)
//								.addId(UUID.randomUUID().toString())
//								.addComando("index")
//								.addJson(this.prepareJson(folha))
//								.build()
//							);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			});
			
			jBuilder.append(read(anomes).parallelStream().map( folha ->
				BulkBuilder
				.init(name)
				.addId(UUID.randomUUID().toString())
				.addComando("index")
				.addJson(this.prepareJson(folha))
				.build()
			)
			.collect(Collectors.joining()));
			
			Request request = new Request("POST", "_bulk");
			HttpEntity entity = new NStringEntity(jBuilder.toString(), ContentType.APPLICATION_JSON);
			request.setEntity(entity);
			Response response = client.performRequest(request);
			System.out.println(response.getStatusLine().getReasonPhrase());
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println("FIM");
			this.close(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void indexarTodos() {
		try {
			final RestClient client = this.openConnection2();
			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			String name = mIndex.name();
			
			
			File folder = new File(prop.getCsv());
			for (File file : folder.listFiles()) {
				String absolutePath = file.getAbsolutePath();
				if(!absolutePath.contains(".csv")) {
					continue;
				}
				this.executor.submit(() -> {
					at.incrementAndGet();
					System.out.println(at.get());
					StringBuilder jBuilder = new StringBuilder();
					try (Reader reader = Files.newBufferedReader(Paths.get(absolutePath));) {
						List<Folha> parse = new CsvToBeanBuilder(reader).withType(Folha.class).withSeparator(';')
								.withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSkipLines(0).build().parse();
						
						jBuilder.append(parse.parallelStream().map( folha ->
							BulkBuilder
							.init(name)
							.addId(UUID.randomUUID().toString())
							.addComando("index")
							.addJson(this.prepareJson(folha))
							.build()
						)
						.collect(Collectors.joining()));
						
						Request request = new Request("POST", "_bulk");
						HttpEntity entity = new NStringEntity(jBuilder.toString(), ContentType.APPLICATION_JSON);
						request.setEntity(entity);
						Response response = client.performRequest(request);
						System.out.println(response.getStatusLine().getReasonPhrase());
						System.out.println(response.getStatusLine().getStatusCode());
						System.out.println("FIM");
						at.decrementAndGet();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				});
			}
			
			while(at.get() > 0) {
				
			}
			
			this.close(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public void test() {
//		try {
//			final RestHighLevelClient client = this.openConnection();
//			BulkRequest request = new BulkRequest(); 
//			request.add(new IndexRequest("posts", "posts", "1")  
//			        .source(XContentType.JSON,"field", "foo"));
//			request.add(new IndexRequest("posts", "posts", "2")  
//			        .source(XContentType.JSON,"field", "bar"));
//			request.add(new IndexRequest("posts", "posts", "3")  
//			        .source(XContentType.JSON,"field", "baz"));
//
//			client.bulk(request);
//			this.close(client);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	@SuppressWarnings("unused")
	private void prepareMap(Map<String, Object> map, Folha folha) throws IllegalAccessException {
		for (Field field : folha.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(MField.class)) {
				field.setAccessible(true);
				MField mField = field.getAnnotation(MField.class);
				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p)))
					map.put(mField.name(), Double.valueOf(field.get(folha).toString().replaceAll(",", ".")));
				else
					map.put(mField.name(), field.get(folha));
			}
		}

	}

	private String prepareJson(Folha folha)  {
		try {
		StringBuilder jsonB = new StringBuilder("{");
		Field[] declaredFields = folha.getClass().getDeclaredFields();
		for (Field field : declaredFields) {
			if (field.isAnnotationPresent(MField.class)) {
				field.setAccessible(true);
				MField mField = field.getAnnotation(MField.class);
				jsonB.append("\"").append(mField.name()).append("\":\"");
				Object obj = field.get(folha);
				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p))) {
					jsonB.append(Double.valueOf(obj.toString().replaceAll(",", ".")));
				} else {
					jsonB.append(obj);
				}
				jsonB.append("\"");

				jsonB.append(",");
			}
		}
		int size = jsonB.length();
		jsonB.replace(size - 1, size, "}");

		return jsonB.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		throw new IllegalAccessError();
	}

//	private Object[] prepareJson(Folha folha) throws IllegalAccessException, InterruptedException {
//		Field[] declaredFields = folha.getClass().getDeclaredFields();
//		List<Object> lista = new ArrayList<Object>();
//		for (Field field : declaredFields) {
//			if (field.isAnnotationPresent(MField.class)) {
//				field.setAccessible(true);
//				MField mField = field.getAnnotation(MField.class);
//				lista.add(mField.name());
//				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p)))
//					lista.add(Double.valueOf(field.get(folha).toString().replaceAll(",", ".")));
//				else
//					lista.add(field.get(folha));
//			}
//		}
//		return lista.toArray();
//	}

	public static void main(String[] args) throws IOException {
		new FolhaService().indexar("201807");
	}
}
