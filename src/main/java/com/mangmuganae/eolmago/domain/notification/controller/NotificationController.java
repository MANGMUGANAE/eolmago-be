package com.mangmuganae.eolmago.domain.notification.controller;

import com.mangmuganae.eolmago.domain.notification.docs.NotificationControllerSpecification;
import com.mangmuganae.eolmago.domain.notification.dto.NotificationDto;
import com.mangmuganae.eolmago.domain.comment.service.NotificationService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerSpecification {

  private final NotificationService notificationService;

  @Override
  @GetMapping
  public ResponseDTO<List<NotificationDto>> findAllByUserId(@AuthenticationPrincipal
      CustomUserDetails customUserDetails) {

    String uuid = customUserDetails.getUuid();

    List<NotificationDto> notificationDtos = notificationService.findAllByUserId(uuid);

    return ResponseDTO.of(notificationDtos, "알림 조회에 성공하였습니다.");
  }

}
