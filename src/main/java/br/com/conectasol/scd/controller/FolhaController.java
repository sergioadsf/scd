package br.com.conectasol.scd.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.conectasol.scd.crawler.service.CrawlerCSVFolha;
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
			return "ok - "+elapsed;
		} catch (IOException e) {
			return "error";
		}
	}

	@PostMapping(path = "/indexcsv")
	public String indexcsv(String anomes) {
		try {
			long start = System.currentTimeMillis();
			folhaService.indexar(anomes);
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - "+elapsed;
		} catch (Exception e) {
			return e.getMessage();
		}
	}

	@PostMapping(path = "/indexallcsv")
	public String indexallcsv(String anomes) {
		try {
			long start = System.currentTimeMillis();
			folhaService.indexar(anomes);
			long elapsed = System.currentTimeMillis() - start;
			System.out.println(elapsed);
			return "ok - "+elapsed;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
}
