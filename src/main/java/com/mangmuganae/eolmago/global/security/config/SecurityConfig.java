package com.mangmuganae.eolmago.global.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.jwt.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenFilter jwtTokenFilter;
  private final ObjectMapper objectMapper;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // 인증 실패 시 반환할 JSON 응답
    String invalidAuthenticationResponse = objectMapper
        .writeValueAsString(ResponseDTO.of(ErrorCode.INTERNAL_SERVER_ERROR));

    // 인가 실패 시 반환할 JSON 응답
    String invalidAuthorizationResponse = objectMapper
        .writeValueAsString(ResponseDTO.of(ErrorCode.ACCESS_DENIED));

    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/users/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/auth/**"
            ).permitAll()
            .anyRequest().authenticated()
        )
        .exceptionHandling(e -> e
            //인증 실패 시 응답 핸들링
            .authenticationEntryPoint(((request, response, authException) -> {
              response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
              response.setContentType("application/json");
              response.getWriter().write(invalidAuthenticationResponse);
            }
            ))
            //인가 실패 시 응답 핸들링
            .accessDeniedHandler((request, response, authException) -> {
              response.setStatus(HttpStatus.UNAUTHORIZED.value());
              response.setContentType("application/json");
              response.getWriter().write(invalidAuthorizationResponse);
            }))
        .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
    ;
    return http.build();
  }
}
