package br.com.conectasol.scd.elastic.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.reflections.Reflections;
import org.springframework.stereotype.Service;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;

@Service
public class CriarIndiceService extends AbsElasticService {

	public void criar() throws IOException {

		RestHighLevelClient client = this.openConnection();
		CreateIndexRequest request = null;

		Reflections reflections = new Reflections("br.com.conectasol.scd.elastic.mapping");
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(MIndex.class);

		for (Class<?> controller : annotated) {
			MIndex index = controller.getAnnotation(MIndex.class);
			if (this.indexExists(client, index.name())) {
				continue;
			}

			request = new CreateIndexRequest(index.name());
			request.settings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2));
			Map<String, Object> properties = new HashMap<>();

			this.criarFields(controller, properties);

			Map<String, Object> mappingMap = new HashMap<>();
			mappingMap.put("properties", properties);
			request.mapping("_doc", mappingMap);
			client.indices().create(request);
		}
		this.close(client);
	}

	private boolean indexExists(RestHighLevelClient client, String indexname) {
		GetIndexRequest request = new GetIndexRequest();
		request.indices(indexname);
		try {
			return client.indices().exists(request);
		} catch (IOException e) {
			return false;
		}
	}

	private void criarFields(Class<?> controller, Map<String, Object> properties) {
		for (Field field : controller.getDeclaredFields()) {
			MField mapping = field.getAnnotation(MField.class);
			if (mapping != null) {
				Map<String, Object> fieldMap = new HashMap<>();
				String type = mapping.type();
				fieldMap.put("type", type);
				if("keyword".equals(type)) {
					fieldMap.put("index", mapping.index());
				}
				String format = mapping.format();
				if (!"".equals(format)) {
					fieldMap.put("format", mapping.format());
				}
				
				properties.put(mapping.name(), fieldMap);
			}
		}
	}
}
