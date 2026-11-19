package com.mangmuganae.eolmago.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://api.eolmago.co.kr", description = "prod"),
        @Server(url = "http://localhost:8080", description = "local")
    }
)

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group("🔐 인증/인가")
        .pathsToMatch("/auth/**")
        .build();
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
        .group("👤 마이페이지")
        .pathsToMatch("/users/**")
        .build();
  }


}
