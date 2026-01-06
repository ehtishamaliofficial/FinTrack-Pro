package com.fintrackpro.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    private static final String SCHEME_NAME = "bearerAuth";
    private static final String SCHEME = "bearer";
    private static final String BEARER_FORMAT = "JWT";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(getApiInfo())
                .components(new Components()
                        .addSecuritySchemes(SCHEME_NAME, createSecurityScheme()))
                .addSecurityItem(new SecurityRequirement().addList(SCHEME_NAME));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-apis")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi authApi() {
        return GroupedOpenApi.builder()
                .group("authentication")
                .pathsToMatch("/api/v1/auth/**")
                .build();
    }

    @Bean
    public GroupedOpenApi walletApi() {
        return GroupedOpenApi.builder()
                .group("wallets")
                .pathsToMatch("/api/v1/wallets/**")
                .build();
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme(SCHEME)
                .bearerFormat(BEARER_FORMAT);
    }

    private Info getApiInfo() {
        return new Info()
                .title("FinTrack Pro API")
                .description("""
                        ### Welcome to FinTrack Pro API Documentation
                        
                        This is the official API documentation for FinTrack Pro, a comprehensive personal finance management system.
                        
                        #### Authentication
                        - Use the `/api/v1/auth/login` endpoint to obtain a JWT token
                        - Click the **Authorize** button and enter your token in the format: `Bearer <your_token>`
                        
                        #### Rate Limiting
                        - API is rate limited to 1000 requests per minute per IP address
                        
                        #### Error Handling
                        - All error responses follow a standard format with appropriate HTTP status codes
                        - Detailed error messages are provided in the response body
                        """)
                .version("1.0.0")
                .contact(new Contact()
                        .name("FinTrack Pro Support")
                        .email("support@fintrackpro.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("https://www.apache.org/licenses/LICENSE-2.0.html"));
    }
}
