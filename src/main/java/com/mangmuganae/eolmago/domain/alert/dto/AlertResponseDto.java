package com.mangmuganae.eolmago.domain.alert.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AlertResponseDto {

  private List<CategoryAlertDto> categories;
  private List<KeywordAlertDto> keywords;

  @Builder
  private AlertResponseDto(List<CategoryAlertDto> categories, List<KeywordAlertDto> keywords) {
    this.categories = categories;
    this.keywords = keywords;
  }

  public static AlertResponseDto of(List<CategoryAlertDto> categories,
      List<KeywordAlertDto> keywords) {

    return AlertResponseDto.builder()
        .categories(categories)
        .keywords(keywords)
        .build();
  }

}
