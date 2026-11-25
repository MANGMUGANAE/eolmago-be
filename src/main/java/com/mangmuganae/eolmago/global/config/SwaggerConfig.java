package com.mangmuganae.eolmago.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://api.eolmago.co.kr", description = "prod"),
        @Server(url = "http://localhost:8080", description = "local")
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Authorization 헤더에 토큰을 넣습니다. /auth/refresh 는 refresh 토큰을 사용합니다."
)

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi authApi() {
    return GroupedOpenApi.builder()
        .group("auth")
        .displayName("🔐 인증/인가")
        .pathsToMatch("/api/v1/auth/**")
        .build();
  }

  @Bean
  public GroupedOpenApi userApi() {
    return GroupedOpenApi.builder()
        .group("users")
        .displayName("👤 마이페이지")
        .pathsToMatch("/api/v1/users/**")
        .build();
  }

  @Bean
  public GroupedOpenApi categoryApi() {
    return GroupedOpenApi.builder()
        .group("category")
        .displayName("🗂️ 카테고리")
        .pathsToMatch("/api/v1/categories/**")
        .build();
  }

  @Bean
  public GroupedOpenApi dealPostApi() {
    return GroupedOpenApi.builder()
        .group("deal-posts")
        .displayName("🔥 핫딜")
        .pathsToMatch("/api/v1/deal-posts/**")
        .build();
  }

  @Bean
  public GroupedOpenApi storeApi() {
    return GroupedOpenApi.builder()
        .group("stores")
        .displayName("🏬 판매처")
        .pathsToMatch("/api/v1/stores/**")
        .build();
  }





}
