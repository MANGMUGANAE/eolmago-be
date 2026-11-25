package com.mangmuganae.eolmago.domain.user.controller;

import com.mangmuganae.eolmago.domain.user.docs.UserControllerSpecification;
import com.mangmuganae.eolmago.domain.user.dto.SignupRequest;
import com.mangmuganae.eolmago.domain.user.service.UserService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSpecification {

  private final UserService userService;

  @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseDTO<?> signup(
      @RequestPart("request") SignupRequest signupRequest,
      @RequestPart("file") MultipartFile file,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    userService.signup(signupRequest, file, uuid);
    return ResponseDTO.of("회원가입에 성공하였습니다.");
  }

  @Override
  @GetMapping("/health")
  public ResponseDTO<String> healthCheck() {

    return ResponseDTO.of("OK");

  }
}
