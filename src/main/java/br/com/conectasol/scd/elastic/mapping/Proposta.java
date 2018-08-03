package br.com.conectasol.scd.elastic.mapping;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;

@MIndex(name = "proposta")
public class Proposta {

	@MField(name = "name", type = "text")
	private String nome;

	@MField(name = "content", type = "text")
	private String conteudo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

}
