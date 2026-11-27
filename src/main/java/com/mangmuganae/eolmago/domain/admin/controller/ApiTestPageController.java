package com.mangmuganae.eolmago.domain.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 구현된 API 를 브라우저에서 순서대로 호출해볼 수 있는 테스트 페이지를 서빙합니다.
 * 개발 확인용이며 비즈니스 로직은 없습니다.
 */
@Controller
public class ApiTestPageController {

  @GetMapping("/test")
  public String apiTestPage() {
    return "api-test";
  }
}
