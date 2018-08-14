package br.com.conectasol.scd.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config.properties")
@ConfigurationProperties(prefix = "path")
public class PathProperties {

	private String csv;
	private String urlfolha;

	public String getCsv() {
		return csv;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public String getUrlfolha() {
		return urlfolha;
	}

	public void setUrlfolha(String urlfolha) {
		this.urlfolha = urlfolha;
	}

}
