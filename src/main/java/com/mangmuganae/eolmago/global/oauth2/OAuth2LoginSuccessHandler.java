package com.mangmuganae.eolmago.global.oauth2;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {


        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

        Map<String, Object> attributes = oAuth2User.getAttributes();


        String accessToken = (String) attributes.get("accessToken");
        String refreshToken = (String) attributes.get("refreshToken");

        String redirectUri = (String) request.getSession().getAttribute("redirectUri");
        request.getSession().removeAttribute("redirectUri");

        //TODO: 리다이렉트 URI 검증 로직 추가해야함

//        if(!StringUtils.hasText(redirectUri)){
//            redirectUri = "https://api.koning.kr/login/social-info";
//            //redirectUri = "http://localhost:8080/login/social-info";
//        }

        response.sendRedirect(redirectUri + "?accessToken=" + accessToken + "&refreshToken=" + refreshToken);
    }
}
