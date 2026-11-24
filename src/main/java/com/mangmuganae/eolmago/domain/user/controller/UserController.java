package com.mangmuganae.eolmago.domain.user.controller;

import com.mangmuganae.eolmago.domain.user.docs.UserControllerSpecification;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSpecification {

  @Override
  @GetMapping("/health")
  public ResponseDTO<String> healthCheck() {

    return ResponseDTO.of("OK");

  }
}
