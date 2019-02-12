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

import br.com.conectasol.scd.annotation.Fielddata;
import br.com.conectasol.scd.annotation.Keyword;
import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;

@Service
public class CriarIndiceService extends AbsElasticService {

	public void criar() throws IOException {

		RestHighLevelClient client = this.openHighConnection();
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
			Map<String, Object> fieldMap = new HashMap<>();
			if (mapping != null) {
				String type = mapping.type();
				fieldMap.put("type", type);
				if("keyword".equals(type)) {
					fieldMap.put("index", mapping.index());
				}
				if("text".equals(type)) {
					this.criarKeyword(field, fieldMap, mapping);
					this.criarFielddata(field, fieldMap, mapping);
				}
				String format = mapping.format();
				if (!"".equals(format)) {
					fieldMap.put("format", mapping.format());
				}
				
				properties.put(mapping.name(), fieldMap);
			} else {
				this.criarKeyword(field, fieldMap, null);
			}
		}
	}

	private void criarKeyword(Field field, Map<String, Object> fieldMap, MField mapping) {
		Keyword keyword = field.getAnnotation(Keyword.class);
		if(keyword != null) {
			Map<String, Object> param = new HashMap<>();
			if(mapping != null) {
				Map<String, Object> kwmap = new HashMap<>();
				fieldMap.put("fields", kwmap);
				kwmap.put("keyword", param);
			}
			
			param.put("type", "keyword");
		}
	}
	
	private void criarFielddata(Field field, Map<String, Object> fieldMap, MField mapping) {
		Fielddata keyword = field.getAnnotation(Fielddata.class);
		if(keyword != null) {
			if(mapping != null) {
				fieldMap.put("fielddata", true);
			}
			
		}
	}
}
