package com.andromedaastroshop.crudfullstack.crud_fullstack.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI api() {
        return new OpenAPI().info(new Info().title("Andromeda AstroShop Ecommerce API").version("1.0").description("Ecommerce para venta de telescopios, tickets, etc."));
    }
}
