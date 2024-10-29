package com.notemate.app.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .addSecurityItem(createSecurityRequirement())
                .components(createComponents())
                .tags(List.of(
                        new Tag().name("User Operations API"),
                        new Tag().name("Authentication API (Public)")
                ))
                .servers(createServers());
    }

    private Info createApiInfo() {
        return new Info()
                .title("Notemate App API")
                .version("1.0")
                .description("""
                        API reference for developers
                        
                        ### App Overview
                        Notemate is a user-friendly application that helps people to take notes from anywhere and anytime.
                        
                        ### Contact
                        Email: niraj.surve07@gmail.com \
                        GitHub: https://github.com/niraj-surve
                        """)
                .contact(createContact());
    }

    private Contact createContact() {
        return new Contact()
                .name("Niraj Surve")
                .url("https://github.com/niraj-surve")
                .email("niraj.surve07@gmail.com");
    }

    private SecurityRequirement createSecurityRequirement() {
        return new SecurityRequirement().addList("bearerAuth");
    }

    private Components createComponents() {
        return new Components()
                .addSecuritySchemes("bearerAuth", createSecurityScheme());
    }

    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    private List<io.swagger.v3.oas.models.servers.Server> createServers() {
        return List.of(
                new io.swagger.v3.oas.models.servers.Server()
                        .url("http://localhost:8080")
                        .description("Local server"),
                new io.swagger.v3.oas.models.servers.Server()
                        .url("https://notemate.herokuapp.com")
                        .description("Production server")
        );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/v3/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v3/api-docs/");
    }
}
