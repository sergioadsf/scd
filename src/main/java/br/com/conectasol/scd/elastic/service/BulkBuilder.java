package br.com.conectasol.scd.elastic.service;

public class BulkBuilder {

	private String indice;
	private String id;
	private String comando;
	private String json;
	
	public static final String FORMAT = "{ \"%s\" : {\"_id\" : \"%s\", \"_type\" : \"_doc\", \"_index\" : \"%s\"} } %n %s %n";

	private BulkBuilder(String indice) {
		this.indice = indice;
	}

	public static BulkBuilder init(String indice) {
		return new BulkBuilder(indice);
	}

	public BulkBuilder addId(String id) {
		this.id = id;
		return this;
	}

	public BulkBuilder addComando(String comando) {
		this.comando = comando;
		return this;
	}

	public BulkBuilder addJson(String json) {
		this.json = json;
		return this;
	}

	public String build() {
		return String.format(FORMAT, comando, id, indice, json);
	}

}
