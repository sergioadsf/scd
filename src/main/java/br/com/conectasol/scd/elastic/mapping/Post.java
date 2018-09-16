package br.com.conectasol.scd.elastic.mapping;

import br.com.conectasol.scd.annotation.MField;
import br.com.conectasol.scd.annotation.MIndex;

@MIndex(name = "posts")
public class Post {

	
	@MField(name = "field", type = "text")
	private String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	

}
