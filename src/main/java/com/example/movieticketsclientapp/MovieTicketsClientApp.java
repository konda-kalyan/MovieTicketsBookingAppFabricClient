package com.example.movieticketsclientapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class MovieTicketsClientApp {
	
		public static void main(String[] args) throws Exception {
		SpringApplication.run(MovieTicketsClientApp.class, args);		
	}
	
	// Swagger Bean function
	@Bean
    public Docket SwaggerBeanAPI1() {
            return new Docket(DocumentationType.SWAGGER_2).select()
                    .apis(RequestHandlerSelectors.basePackage("com.example.movieticketsclientapp")).build();
    }
}
