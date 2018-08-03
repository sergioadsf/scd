package br.com.conectasol.scd.listener;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.com.conectasol.scd.elastic.service.CriarIndiceService;

@Component
public class CriarIndicesListener implements ServletContextListener {

	@Autowired
	private CriarIndiceService criarIndice;
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			criarIndice.criar();
		} catch (IOException e) {
			Logger.getLogger("elasticsearch").info(e.getMessage());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("ScanAnnotation.contextDestroyed()");
	}

}
