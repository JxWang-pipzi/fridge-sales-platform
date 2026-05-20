package com.fridge.sales.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI Configuration
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI fridgeSalesOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Fridge Sales API")
                        .description("Backend API for Fridge Sales Website")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
