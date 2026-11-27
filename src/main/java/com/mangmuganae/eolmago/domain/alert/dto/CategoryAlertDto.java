package com.mangmuganae.eolmago.domain.alert.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryAlertDto {

  private Long alertId;
  private Long categoryId;
  private String categoryName;
  private boolean enabled;

  @Builder
  private CategoryAlertDto(Long alertId, Long categoryId, String categoryName, boolean enabled) {
    this.alertId = alertId;
    this.categoryId = categoryId;
    this.categoryName = categoryName;
    this.enabled = enabled;
  }

  public static CategoryAlertDto of(Long alertId, Long categoryId, String categoryName) {
    return CategoryAlertDto.builder()
        .alertId(alertId)
        .categoryId(categoryId)
        .categoryName(categoryName)
        .enabled(alertId != null)
        .build();
  }

}
