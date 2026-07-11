package com.mangmuganae.eolmago.domain.user.controller;

import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.domain.user.repository.UserRepository;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserRepository userRepository;

  @GetMapping("/health")
  public ResponseDTO<String> healthCheck() {

    return ResponseDTO.of("OK");

  }

  @GetMapping("/test")
  public ResponseDTO<List<User>> findAll() {

    List<User> response = List.of(
        User.of(1L, "리서영"),
        User.of(2L, "홍명보"),
        User.of(3L, "홍당무"),
        User.of(4L, "승빈짱")
    );

    return ResponseDTO.of(response, "조회에 성공하셨습니다.");

  }
}
