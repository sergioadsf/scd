package br.com.conectasol.scd.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.conectasol.scd.elastic.service.IndexarDoc;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping(path = "proposta")
public class PropostaController {

	@Autowired
	private IndexarDoc indexaDoc;

	@PostMapping(path = "/indexLote")
	private String indexar() throws Exception {
		indexaDoc.indexarLote();
		return "ok";
	}

	@ApiOperation(value = "Upload de documentos", notes = "Upload de documentos")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 404, message = "Not Found"),
			@ApiResponse(code = 500, message = "Failure") })
	@PostMapping(path = "/index", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE }, headers = "accept=multipart/form-data")
	private String indexar(
			@ApiParam(value = "Documentos", allowMultiple = true, required = true) 
			@RequestPart(value = "file", name = "file", required = true) MultipartFile[] file)
			throws Exception {
		for (MultipartFile f : file) {
			System.out.println(f.getOriginalFilename());
		}
		return "ok";
	}
	
	@GetMapping(path = "consultar")
	private String consultar(String field) throws IOException {
		return indexaDoc.consultar(field);
	}
}
