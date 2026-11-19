package com.mangmuganae.eolmago.domain.user.controller;

import com.mangmuganae.eolmago.domain.user.docs.UserControllerSpecification;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController implements UserControllerSpecification {

  private final UserRepository userRepository;

  @Override
  @GetMapping("/health")
  public ResponseDTO<String> healthCheck() {

    return ResponseDTO.of("OK");

  }
}
