//package br.com.conectasol.scd.elastic.service;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.UUID;
//
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.stereotype.Service;
//
//import br.com.conectasol.scd.doc.service.PDFExtractor;
//
//@Service
//public class IndexarDoc extends AbsElasticService {
//
//	private static final String PATH = "/home/sergio/Downloads/arquivos_para_indexar/";
//
//	public void indexarLote() throws Exception {
//		final RestHighLevelClient client = this.openConnection();
//
//		PDFExtractor pdfExtractor;
//		File file = new File(PATH);
//		for (File f : file.listFiles()) {
//			String name = f.getName();
//			if (f.isFile() && name.contains(".pdf")) {
//				pdfExtractor = new PDFExtractor(f);
//				
//				Map<String, Object> map = new HashMap<>();
//				map.put("content", pdfExtractor.getDocumentText());
//				map.put("name", f.getName());
//				IndexRequest request = new IndexRequest("proposta", "proposta", UUID.randomUUID().toString()).source(map);
//				client.index(request);
//			}
//		}
//
//		this.close(client);
//	}
//}
