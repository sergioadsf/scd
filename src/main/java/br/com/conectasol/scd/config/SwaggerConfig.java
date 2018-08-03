package br.com.conectasol.scd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)  
		          .select()                                  
		          .apis(RequestHandlerSelectors.basePackage("br.com.conectasol.scd.controller"))              
		          .paths(PathSelectors.any())
		          .build().apiInfo(metaData())
		          .useDefaultResponseMessages(false);
	}
	
	private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Spring Boot 2.0 File Upload")
                .description("Upload file Swagger-ui 2")
                .version("version 1.0")
                .contact(new Contact("Sergio Silva Filho", "https://www.conectasol.com.br", 
                      "sergioadsf@gmail.com")).build();
    }
	
	
}
