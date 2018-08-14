package br.com.conectasol.scd.elastic.service.folha;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Folha> read(String anomes) throws IOException {
		String p = String.format("%sFolhaPagamento_%s.csv", prop.getCsv(), anomes);

		try (Reader reader = Files.newBufferedReader(Paths.get(p));) {

			return new CsvToBeanBuilder(reader).withType(Folha.class).withSeparator(';')
					.withQuoteChar(CSVWriter.DEFAULT_QUOTE_CHARACTER).withSkipLines(0).build().parse();
		}

	}

	public void indexar(String anomes) throws Exception {
		final RestHighLevelClient client = this.openConnection();

		Map<String, Object> map = new HashMap<>();
		read(anomes).parallelStream().forEach(folha -> {
			try {
				this.prepareMap(map, folha);
				MIndex mIndex = folha.getClass().getAnnotation(MIndex.class);
				String name = mIndex.name();
				IndexRequest request = new IndexRequest(name, name, UUID.randomUUID().toString()).source(map);
				client.index(request);
				map.clear();
			} catch (Exception e) {
			}
		});
		;

		this.close(client);
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

	public static void main(String[] args) throws IOException {
		new FolhaService().read("201806");
	}
}
