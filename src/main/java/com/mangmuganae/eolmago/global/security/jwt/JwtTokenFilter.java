package com.mangmuganae.eolmago.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import com.mangmuganae.eolmago.global.security.exception.InvalidAccessJwtException;
import com.mangmuganae.eolmago.global.security.exception.InvalidRefrashJwtException;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  private final ObjectMapper objectMapper;

  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response, FilterChain filterChain
  ) throws ServletException, IOException {

    String url = request.getRequestURI();
    String method = request.getMethod();

    if(isPublicPath(url)) {
      log.info("[" + request.getRemoteAddr() + "]:"
          + "[" + method + ":" + url + "](allowed)");

      filterChain.doFilter(request, response);
      return;
    }
    String token = jwtTokenProvider.getTokenFromRequest(request);

    if(token != null) {
      try {
        jwtTokenProvider.validateToken(token);
        // TODO: 배포 시 주석 해제해야함
//        JwtTokenType tokenType = jwtTokenProvider.getTypeFromToken(token);
//
//        if (tokenType.equals(JwtTokenType.REFRESH)) {
//          throw new InvalidAccessJwtException(ErrorCode.ACCESS_TOKEN_INVALID);
//        }

        log.info("[" + request.getRemoteAddr() + "]:"
            + "[" + method + ":" + url + "](allowed)");

        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

      } catch (InvalidAccessJwtException e) {
        handleAccessTokenInvalid(response);
        return;

      } catch (InvalidRefrashJwtException e) {
        handleRefreshTokenInvalid(response);
        return;
      }

    } else {
      log.info("[" + request.getRemoteAddr() + "]:"
          + "[" + method + ":" + url + "](no token)");
    }

    filterChain.doFilter(request, response);
  }

  private void handleAccessTokenInvalid(HttpServletResponse response) throws IOException {
    String errorResponse = objectMapper.writeValueAsString(ResponseDTO.of(ErrorCode.ACCESS_TOKEN_INVALID));
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(errorResponse);
  }

  private void handleRefreshTokenInvalid(HttpServletResponse response) throws IOException {
    String errorResponse = objectMapper.writeValueAsString(ResponseDTO.of(ErrorCode.REFRESH_TOKEN_INVALID));
    response.setContentType("application/json;charset=UTF-8");
    response.setStatus(HttpStatus.UNAUTHORIZED.value());
    response.setContentType("application/json");
    response.getWriter().write(errorResponse);
  }

  private void handleUserNotFound(HttpServletResponse response) throws IOException {
    String errorResponse = objectMapper.writeValueAsString(ResponseDTO.of(ErrorCode.USER_NOT_FOUND));
    response.setStatus(ErrorCode.USER_NOT_FOUND.getActualStatusCode());
    response.setContentType("application/json");
    response.getWriter().write(errorResponse);
  }


  private boolean isPublicPath(String path) {
    return path.equals("/test") ||
        path.startsWith("/login/oauth2/code") ||
        path.startsWith("/swagger-ui") ||
        path.startsWith("/api/v1/auth/refresh") ||
        path.startsWith("/files") ||
        path.startsWith("/v3/api-docs");
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token) {

    String userUuid = jwtTokenProvider.getUserUuidFromToken(token);
    User user = userRepository.findByUuid(userUuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );
    UserDetails userDetails = new CustomUserDetails(user);

    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
  }
}
