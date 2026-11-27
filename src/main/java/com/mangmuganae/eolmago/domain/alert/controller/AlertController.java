package com.mangmuganae.eolmago.domain.alert.controller;


import com.mangmuganae.eolmago.domain.alert.docs.AlertControllerSpecification;
import com.mangmuganae.eolmago.domain.alert.dto.AlertDto;
import com.mangmuganae.eolmago.domain.alert.dto.AlertResponseDto;
import com.mangmuganae.eolmago.domain.alert.service.AlertService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import com.mangmuganae.eolmago.global.security.core.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/alerts")
public class AlertController implements AlertControllerSpecification {

  private final AlertService alertService;

  @Override
  @GetMapping
  public ResponseDTO<AlertResponseDto> findMyAlerts(
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    AlertResponseDto alertResponseDto = alertService.findMyAlerts(uuid);

    return ResponseDTO.of(alertResponseDto, "알림 설정 조회에 성공하였습니다.");
  }

  @Override
  @PostMapping
  public ResponseDTO<?> registerAlert(@Valid @RequestBody AlertDto alertDto,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    alertService.registerAlert(alertDto, uuid);
    return ResponseDTO.of("알림 등록에 성공하였습니다.");
  }


  @Override
  @DeleteMapping("/{alertId}")
  public ResponseDTO<?> deleteAlert(@PathVariable Long alertId,
      @AuthenticationPrincipal CustomUserDetails userDetails) {

    String uuid = userDetails.getUuid();

    alertService.deleteAlert(alertId, uuid);

    return ResponseDTO.of("알림 삭제에 성공하였습니다.");
  }

}
