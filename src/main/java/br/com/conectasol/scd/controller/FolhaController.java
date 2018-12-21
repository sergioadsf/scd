package br.com.conectasol.scd.controller;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conctasol.annotation.util.IndiceUtil;
import br.com.conectasol.scd.annotation.MIndex;
import br.com.conectasol.scd.crawler.service.CrawlerCSVFolha;
import br.com.conectasol.scd.elastic.mapping.Folha;
import br.com.conectasol.scd.elastic.service.folha.FolhaService;

@RestController
@RequestMapping(path = "folha")
public class FolhaController {

	@Autowired
	private CrawlerCSVFolha cFolha;

	@Autowired
	private FolhaService folhaService;

	@PostMapping(path = "/cfolha")
	public String runCFolha() {
		try {
			long start = System.currentTimeMillis();
			cFolha.buscarArquivos();
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - " + elapsed;
		} catch (IOException e) {
			return "error";
		}
	}

	@PostMapping(path = "/indexcsv")
	public String indexcsv(String anomes) {
		try {
			long start = System.currentTimeMillis();
			folhaService.indexar(anomes);
//			folhaService.test();
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - " + elapsed;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@PostMapping(path = "/consultar")
	public String consultar(String nome, int tamanho) {
		try {
			long start = System.currentTimeMillis();
			String body = folhaService.consultar(nome, tamanho);
//			folhaService.test();
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - " + elapsed + "\n\n" + body;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@PostMapping(path = "/indexallcsv")
	public String indexallcsv() {
		try {
			long start = System.currentTimeMillis();
			folhaService.indexarTodos();
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - " + elapsed;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@GetMapping(path = "/teste")
	public String teste() {
		try {

			MIndex mIndex = Folha.class.getAnnotation(MIndex.class);
			String nome = "";
			if(mIndex != null) {
				nome = mIndex.name();
			}
			HttpClient httpclient = HttpClients.createDefault();
			HttpPost httppost = new HttpPost("http://localhost:8083/indice/" + nome);
			httppost.setHeader("Accept", "application/json");

			JSONObject json = new JSONObject(new IndiceUtil().convert(Folha.class));
			
			StringEntity entity = new StringEntity(json.toString(), "UTF8");
			entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);

			return "ok - ";
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
