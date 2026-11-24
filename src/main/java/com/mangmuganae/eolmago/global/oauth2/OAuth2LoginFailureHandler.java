package com.mangmuganae.eolmago.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

  private static final String RETRY_FLAG = "OAUTH2_RETRIED";
  private static final String DEFAULT_REDIRECT_URI = "https://google.com";

  @Override
  public void onAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception) throws IOException {

    String errorCode = (exception instanceof OAuth2AuthenticationException e)
        ? e.getError().getErrorCode()
        : "unknown";

    log.warn("OAuth2 로그인 실패: {} / {}", errorCode, exception.getMessage());

    HttpSession session = request.getSession();

    // 뒤로가기 후 재선택 등으로 state가 안 맞는 경우 → 한 번만 자동 재시도
    if ("authorization_request_not_found".equals(errorCode)
        && session.getAttribute(RETRY_FLAG) == null) {

      String provider = extractProvider(request.getRequestURI()); // google, kakao
      if (provider != null) {
        session.setAttribute(RETRY_FLAG, true);
        response.sendRedirect("/oauth2/authorization/" + provider);
        return;
      }
    }

    // 재시도도 실패했거나 다른 에러(사용자 취소 access_denied 등)
    session.removeAttribute(RETRY_FLAG);

    String redirectUri = (String) session.getAttribute("redirectUri");
    session.removeAttribute("redirectUri");

    if (!StringUtils.hasText(redirectUri)) {
      redirectUri = DEFAULT_REDIRECT_URI;
    }

    String target = UriComponentsBuilder.fromUriString(redirectUri)
        .queryParam("error", errorCode)
        .build().encode().toUriString();

    response.sendRedirect(target);
  }

  // /login/oauth2/code/kakao → kakao
  private String extractProvider(String uri) {
    int idx = uri.lastIndexOf('/');
    return (idx >= 0 && idx < uri.length() - 1) ? uri.substring(idx + 1) : null;
  }
}