package br.com.conectasol.scd.elastic.mapping;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;

@MIndex(name = "pessoa")
public class Pessoa {

	@MField(name = "name", type = "text")
	private String nome;

	@MField(name = "age", type = "integer")
	private Integer idade;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getIdade() {
		return idade;
	}

	public void setIdade(Integer idade) {
		this.idade = idade;
	}

}
