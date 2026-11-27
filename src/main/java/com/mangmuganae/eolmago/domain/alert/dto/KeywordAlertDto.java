package com.mangmuganae.eolmago.domain.alert.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class KeywordAlertDto {

  private Long alertId;
  private String keyword;

  @Builder
  private KeywordAlertDto(Long alertId, String keyword) {
    this.alertId = alertId;
    this.keyword = keyword;
  }

  public static KeywordAlertDto of(Long alertId, String keyword) {
    return KeywordAlertDto.builder()
        .alertId(alertId)
        .keyword(keyword)
        .build();
  }

}
