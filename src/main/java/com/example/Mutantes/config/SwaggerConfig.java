package com.example.Mutantes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detector API")
                        .version("1.0")
                        .description("API REST para detectar si un humano es mutante basándose en su secuencia de ADN")
                        .contact(new Contact()
                                .name("API Support")
                                .email("support@mutantdetector.com")));
    }
}

