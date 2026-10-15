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
        User.of(1L, "김민준"),
        User.of(2L, "이서연"),
        User.of(3L, "박도현"),
        User.of(4L, "최지아"),
        User.of(5L, "정우진"),
        User.of(6L, "강하은"),
        User.of(7L, "조시우"),
        User.of(8L, "윤서아"),
        User.of(9L, "장예준"),
        User.of(10L, "임지유"),
        User.of(11L, "한주원"),
        User.of(12L, "오채은"),
        User.of(13L, "서건우"),
        User.of(14L, "신다은"),
        User.of(15L, "권현우"),
        User.of(16L, "황수빈"),
        User.of(17L, "안지호"),
        User.of(18L, "송예린"),
        User.of(19L, "전민재"),
        User.of(20L, "홍소율"),
        User.of(21L, "유태윤"),
        User.of(22L, "고나연"),
        User.of(23L, "문승현"),
        User.of(24L, "양가은"),
        User.of(25L, "손재원"),
        User.of(26L, "배윤아"),
        User.of(27L, "백준서"),
        User.of(28L, "허지민"),
        User.of(29L, "남궁선"),
        User.of(30L, "노하린"),
        User.of(31L, "심동하"),
        User.of(32L, "곽세아"),
        User.of(33L, "성유찬"),
        User.of(34L, "차은서"),
        User.of(35L, "주한결"),
        User.of(36L, "우다인"),
        User.of(37L, "구민성"),
        User.of(38L, "민서윤"),
        User.of(39L, "진태현"),
        User.of(40L, "나은비"),
        User.of(41L, "지성훈"),
        User.of(42L, "엄소희"),
        User.of(43L, "채준혁"),
        User.of(44L, "원하율"),
        User.of(45L, "천지훈"),
        User.of(46L, "방예나"),
        User.of(47L, "공시원"),
        User.of(48L, "현채린"),
        User.of(49L, "변우석"),
        User.of(50L, "여지안")
    );

    return ResponseDTO.of(response, "조회에 성공하셨습니다.");

  }
}
