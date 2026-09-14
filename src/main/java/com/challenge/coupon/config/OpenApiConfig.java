package com.challenge.coupon.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Coupon Management API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento e ciclo de vida de cupons promocionais.")
                        .contact(new Contact().name("Tech Team").email("tech@challenge.com"))
                        .license(new License().name("Apache 2.0").url("https://spring.io")));
    }
}
