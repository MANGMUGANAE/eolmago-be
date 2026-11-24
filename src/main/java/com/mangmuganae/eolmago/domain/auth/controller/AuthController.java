package com.mangmuganae.eolmago.domain.auth.controller;

import com.mangmuganae.eolmago.domain.auth.dto.RefreshResponseDto;
import com.mangmuganae.eolmago.domain.auth.service.AuthService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/refresh")
  public ResponseDTO<RefreshResponseDto> authRefresh(@AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();



  }

}
