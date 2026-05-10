package com.practice.premiumservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Insurance Premium Calculation API")
                .description("A comprehensive API for calculating insurance premiums based on mileage, vehicle type, and regional factors. This service provides endpoints for premium calculation, data retrieval, and statistical analysis.")
                .version("1.0.0"))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Local development server")));
    }
}
