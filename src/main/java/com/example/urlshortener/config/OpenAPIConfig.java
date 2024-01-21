package com.example.urlshortener.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI(
            @Value("${app.name}") String appTitle,
            @Value("${app.description}") String appDescription,
            @Value("${app.version}") String appVersion) {

        SecurityRequirement securityRequirement = new SecurityRequirement();
        securityRequirement.addList("bearerAuth", Collections.emptyList());
        Components components = new Components();
        components.addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"));
        return new OpenAPI().components(components).security(List.of(securityRequirement))
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion)
                        .description(appDescription));
    }
}
