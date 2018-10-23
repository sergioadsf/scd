package br.com.conectasol.scd.elastic.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.conectasol.scd.annotation.MIndex;
import br.com.conectasol.scd.doc.service.PDFExtractor;
import br.com.conectasol.scd.elastic.mapping.Proposta;
import br.com.conectasol.scd.util.PathProperties;

@Service
public class IndexarDoc extends AbsElasticService {

	@Autowired
	private PathProperties prop;

	public void indexarLote() throws Exception {
		final RestHighLevelClient client = this.openHighConnection();

		PDFExtractor pdfExtractor;
		File file = new File(prop.getPropostas());
		for (File f : file.listFiles()) {
			String name = f.getName();
			if (f.isFile() && name.contains(".pdf")) {
				pdfExtractor = new PDFExtractor(f);

				Map<String, Object> map = new HashMap<>();
				map.put("content", pdfExtractor.getDocumentText());
				map.put("name", f.getName());
				IndexRequest request = new IndexRequest("proposta", "_doc", UUID.randomUUID().toString()).source(map);
				client.index(request);
			}
		}

		this.close(client);
	}

	public String consultar(String field) throws IOException {
		RestClient client = null;
		try {
			client = this.openConnection();
			MIndex mIndex = Proposta.class.getAnnotation(MIndex.class);
			StringBuilder jBuilder = new StringBuilder("{");
			jBuilder.append("\"stored_fields\" : [\"name\"],");
			jBuilder.append("\"query\" : {");
			jBuilder.append("	\"match\": { \"content\": \"").append(field).append("\" }");
			jBuilder.append("},");
			jBuilder.append("\"highlight\" : { ");
			jBuilder.append("	\"fragment_size\" : ").append(150).append(",");
			jBuilder.append("	\"fields\" : { ");
			jBuilder.append("		\"content\" : {}");
			jBuilder.append("}");
			jBuilder.append("}");
			jBuilder.append("}");

			return this.doGet(client, jBuilder, mIndex.name());

		} finally {
			this.close(client);
		}
	}
}
