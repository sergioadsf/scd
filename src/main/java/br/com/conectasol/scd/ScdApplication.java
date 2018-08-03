package br.com.conectasol.scd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("br.com.conectasol.scd")
@ServletComponentScan("br.com.conectasol.scd.listener")
@SpringBootApplication
public class ScdApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScdApplication.class, args);
	}

//	@Bean
//	public ServletListenerRegistrationBean<ServletContextListener> listenerRegistrationBean() {
//		ServletListenerRegistrationBean<ServletContextListener> bean = new ServletListenerRegistrationBean<>();
//		bean.setListener(new ScanAnnotationListener());
//		
//		return bean;
//	}
}
