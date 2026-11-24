package com.mangmuganae.eolmago.domain.auth.service;

import com.mangmuganae.eolmago.domain.auth.dto.RefreshResponseDto;
import com.mangmuganae.eolmago.domain.auth.entity.Auth;
import com.mangmuganae.eolmago.domain.auth.repository.AuthRepository;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import com.mangmuganae.eolmago.global.security.exception.InvalidRefrashJwtException;
import com.mangmuganae.eolmago.global.security.exception.UserNotFoundException;
import com.mangmuganae.eolmago.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;

  private final JwtTokenProvider jwtTokenProvider;

  @Transactional
  public RefreshResponseDto authRefresh(HttpServletRequest request) {

    String oldRefreshToken = jwtTokenProvider.extractToken(request);

    if (!jwtTokenProvider.isValidToken(oldRefreshToken)) {
      throw new InvalidRefrashJwtException(ErrorCode.REFRESH_TOKEN_INVALID);
    }

    String uuid = jwtTokenProvider.getUserUuidFromToken(oldRefreshToken);

    Auth auth = authRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.REFRESH_TOKEN_INVALID)
    );

    User user = userRepository.findByUuid(uuid).orElseThrow(
        () -> new UserNotFoundException(ErrorCode.USER_NOT_FOUND)
    );

    if (!jwtTokenProvider.isValidToken(auth.getRefreshToken())) {
      authRepository.delete(auth);
      throw new InvalidRefrashJwtException(ErrorCode.REFRESH_TOKEN_INVALID);
    }

    if (!auth.getRefreshToken().equals(oldRefreshToken)) {
      throw new InvalidRefrashJwtException(ErrorCode.REFRESH_TOKEN_INVALID);
    }

    String newAccessToken = jwtTokenProvider.createAccessToken(uuid, user.getRole().toString());
    String newRefreshToken = jwtTokenProvider.createRefreshToken(uuid, user.getRole().toString());

    auth.updateRefreshToken(newRefreshToken);

    return RefreshResponseDto.of(newAccessToken, newRefreshToken);
  }
}
