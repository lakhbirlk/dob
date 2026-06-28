package com.dob.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("DataOfBusiness API")
                .version("1.0.0")
                .description("""
                    Corporate Intelligence Platform — REST API for company research,
                    due-diligence, and CA-certified financial data for Indian private companies.

                    ## Key Features
                    - Company database search & filters
                    - CA-certified financial statements
                    - Membership & subscription management
                    - Razorpay payment integration
                    - Grievance & refund management
                    - Admin approval workflows

                    ## Authentication
                    Most endpoints require a JWT Bearer token. Obtain one via `POST /api/auth/login`.
                    """)
                .contact(new Contact()
                    .name("DataOfBusiness Support")
                    .email("support@dataofbusiness.in")
                    .url("https://dataofbusiness.in"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://dataofbusiness.in/terms")))
            .externalDocs(new ExternalDocumentation()
                .description("Platform Documentation")
                .url("https://dataofbusiness.in/docs"))
            .servers(List.of(
                new Server().url("http://localhost:" + serverPort).description("Local development"),
                new Server().url("https://api.dataofbusiness.com").description("Production")
            ))
            .addSecurityItem(new SecurityRequirement().addList("Bearer"))
            .components(new Components()
                .addSecuritySchemes("Bearer", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter your JWT token. Obtain one via `POST /api/auth/login`.")));
    }
}
