package com.mangmuganae.eolmago.global.oauth2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;   // 추가
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.stereotype.Component;

@Slf4j   // 추가
@Component
public class MultiStateAuthorizationRequestRepository
    implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

  private static final String SESSION_KEY = "OAUTH2_AUTHORIZATION_REQUESTS";
  private static final int MAX_REQUESTS = 5;

  @Override
  public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
    String state = request.getParameter(OAuth2ParameterNames.STATE);
    if (state == null) {
      return null;
    }
    Map<String, OAuth2AuthorizationRequest> requests = getRequests(request, false);
    return requests != null ? requests.get(state) : null;
  }

  @Override
  public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (authorizationRequest == null) {
      removeAuthorizationRequest(request, response);
      return;
    }
    Map<String, OAuth2AuthorizationRequest> requests = getRequests(request, true);
    requests.put(authorizationRequest.getState(), authorizationRequest);

    while (requests.size() > MAX_REQUESTS) {
      String oldest = requests.keySet().iterator().next();
      requests.remove(oldest);
    }
    request.getSession().setAttribute(SESSION_KEY, requests);

    // 추가: 로그인 시작할 때
    log.info("[OAuth2 저장] sessionId={}, state={}, 목록={}",
        request.getSession().getId(), authorizationRequest.getState(), requests.keySet());
  }

  @Override
  public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
      HttpServletResponse response) {
    String state = request.getParameter(OAuth2ParameterNames.STATE);
    Map<String, OAuth2AuthorizationRequest> requests = getRequests(request, false);

    HttpSession session = request.getSession(false);

    if (state == null || requests == null) {
      return null;
    }
    OAuth2AuthorizationRequest removed = requests.remove(state);
    request.getSession().setAttribute(SESSION_KEY, requests);

    return removed;
  }

  @SuppressWarnings("unchecked")
  private Map<String, OAuth2AuthorizationRequest> getRequests(HttpServletRequest request, boolean create) {
    HttpSession session = request.getSession(create);
    if (session == null) {
      return null;
    }
    Map<String, OAuth2AuthorizationRequest> requests =
        (Map<String, OAuth2AuthorizationRequest>) session.getAttribute(SESSION_KEY);
    if (requests == null && create) {
      requests = new LinkedHashMap<>();
    }
    return requests;
  }
}