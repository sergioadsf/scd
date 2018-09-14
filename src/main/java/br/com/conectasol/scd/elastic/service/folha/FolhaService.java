package br.com.conectasol.scd.elastic.service.folha;

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
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;
import br.com.conectasol.scd.elastic.mapping.Folha;
import br.com.conectasol.scd.elastic.service.AbsElasticService;
import br.com.conectasol.scd.util.PathProperties;

@Service
public class FolhaService extends AbsElasticService {

	@Autowired
	private PathProperties prop;

	@PostConstruct
	public void init() {
		this.executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Folha> read(String anomes) throws IOException {
		String p = String.format("%sFolhaPagamento_%s.csv", prop.getCsv(), anomes);

		try (Reader reader = Files.newBufferedReader(Paths.get(p));) {

			return new CsvToBeanBuilder(reader).withType(Folha.class).withSeparator(';')
					.withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSkipLines(0).build().parse();
		}

	}

	public void indexar(String anomes) {
		try {
			final RestHighLevelClient client = this.openConnection();

			final BulkRequest request = new BulkRequest();
			request.timeout(TimeValue.timeValueMinutes(2));
			read(anomes).parallelStream().forEach(folha -> {
				try {
					MIndex mIndex = folha.getClass().getAnnotation(MIndex.class);
					String name = mIndex.name();
					request.add(new IndexRequest(name, name, UUID.randomUUID().toString()).source("{\"ano_mes\": \" teste\"}", XContentType.JSON));
//					request.add(new IndexRequest(name, name, UUID.randomUUID().toString())
//							.source(XContentFactory.jsonBuilder().startObject().field("ano_mes", "201807")
//									.field("nome_cargo_secundario", "Não se Aplica").endObject()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			client.bulk(request);
			this.close(client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

//	private String prepareJson(Folha folha) throws IllegalAccessException {
//		StringBuilder jsonB = new StringBuilder("{")  ;
//		Field[] declaredFields = folha.getClass().getDeclaredFields();
//		int size = declaredFields.length;
//		XContentBuilder cb = XContentFactory.jsonBuilder()
//        .startObject();
//            .field( "name", "Mark Twain" )
//            .field( "age", 75 )
//        .endObject()
//		for (Field field : declaredFields) {
//			--size;
//			if (field.isAnnotationPresent(MField.class)) {
//				field.setAccessible(true);
//				MField mField = field.getAnnotation(MField.class);
//				jsonB.append("\"").append(mField.name()).append("\":\"");
//				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p)))
//					jsonB.append(Double.valueOf(field.get(folha).toString().replaceAll(",", ".")));
//				else
//					jsonB.append(field.get(folha));
//				jsonB.append("\"");
//				if(size > 0)
//					jsonB.append(",");
//			}
//		}
//		jsonB.append("}");
//		
//		return jsonB.toString();
//	}

//	private String prepareJson(Folha folha) throws IllegalAccessException {
//		StringBuilder jsonB = new StringBuilder("{")  ;
//		Field[] declaredFields = folha.getClass().getDeclaredFields();
//		int size = declaredFields.length;
//		for (Field field : declaredFields) {
//			--size;
//			if (field.isAnnotationPresent(MField.class)) {
//				field.setAccessible(true);
//				MField mField = field.getAnnotation(MField.class);
//				jsonB.append("\"").append(mField.name()).append("\":\"");
//				if (Stream.of("double", "float").anyMatch(p -> mField.type().equals(p)))
//					jsonB.append(Double.valueOf(field.get(folha).toString().replaceAll(",", ".")));
//				else
//					jsonB.append(field.get(folha));
//				jsonB.append("\"");
//				if(size > 0)
//					jsonB.append(",");
//			}
//		}
//		jsonB.append("}");
//		
//		return jsonB.toString();
//	}

	public static void main(String[] args) throws IOException {
		new FolhaService().read("201806");
	}
}
