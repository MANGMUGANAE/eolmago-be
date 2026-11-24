package com.mangmuganae.eolmago.global.oauth2;

import com.mangmuganae.eolmago.domain.auth.entity.Auth;
import com.mangmuganae.eolmago.domain.auth.repository.AuthRepository;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.security.jwt.JwtTokenProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

   private final JwtTokenProvider jwtTokenProvider;
   private final UserRepository userRepository;
   private final AuthRepository authRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

      boolean isNewUser = false;
      String accessToken;
      String refreshToken;
      String nameAttributeKey;

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String socialProvider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email, name, providerId;

        Map<String, Object> customAttributes = new HashMap<>(attributes);



      if ("google".equals(socialProvider)) {
        nameAttributeKey = "sub";
        providerId = (String) attributes.get("sub");

        email = (String) attributes.get("email");
        name = (String) attributes.get("name");

        log.info("Google Provider ID: " + providerId);
        log.info("Google Email: " + email);
        log.info("Google Name: " + name);

      }  else if ("kakao".equals(socialProvider)) {
        nameAttributeKey = "id";
        providerId = String.valueOf(attributes.get("id"));

//        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
//        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
//
//        name = (String) profile.get("nickname");
//        email = name + "@" + "kakao.com";

        log.info("Kakao Provider ID: " + providerId);
      } else {
        throw new OAuth2AuthenticationException("지원되지 않는 소셜 서비스 입니다. : " + socialProvider);
      }

        // 회원가입 또는 로그인 처리
      User user = userRepository.findByProviderId(providerId)
          .orElse(null);

      if (user == null) {
        user = User.createMember(
            providerId,
            socialProvider,
            null
        );
        isNewUser = true;
      }

      if(isNewUser) {
        accessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getRole().toString());
        refreshToken = jwtTokenProvider.createRefreshToken(user.getUuid(), user.getRole().toString());

        authRepository.save(
            Auth.of(user.getUuid(), refreshToken)
        );
        userRepository.save(user);
      } else {
        User cpyUser = user;

        accessToken = jwtTokenProvider.createAccessToken(user.getUuid(), user.getRole().toString());
        log.info("uuid : " + user.getUuid());
        Auth auth = authRepository.findByUuid(user.getUuid())
            .orElseGet(() -> {
              Auth newAuth = Auth.of(
                  cpyUser.getUuid(),
                  jwtTokenProvider.createRefreshToken(cpyUser.getUuid(), cpyUser.getRole().toString())
              );
              return authRepository.save(newAuth);
            });

        refreshToken = auth.getRefreshToken();

        if(!jwtTokenProvider.isValidToken(refreshToken)) {
          refreshToken = jwtTokenProvider.createRefreshToken(cpyUser.getUuid(), cpyUser.getRole().toString());
          auth.updateRefreshToken(refreshToken);
          authRepository.save(auth);
        }
      }

        // JWT 액세스 & 리프레시 토큰 발급

        customAttributes.put("accessToken", accessToken);
        customAttributes.put("refreshToken", refreshToken);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString())),
                customAttributes,
            nameAttributeKey
        );
    }
}