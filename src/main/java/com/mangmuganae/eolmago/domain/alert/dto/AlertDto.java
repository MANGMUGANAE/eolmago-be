package com.mangmuganae.eolmago.domain.alert.dto;

import com.mangmuganae.eolmago.domain.alert.entity.Alert;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertDto {

  private Long categoryId;
  private String keyword;


  @AssertTrue(message = "카테고리와 키워드 중 하나는 반드시 입력해야합니다.")
  private boolean isCategoryOrKeywordPresent() {
    return categoryId != null || (keyword != null && !keyword.isBlank());
  }

  @Builder
  private AlertDto(Long categoryId, String keyword) {
    this.categoryId = categoryId;
    this.keyword = keyword;
  }

  public static AlertDto of(Long categoryId, String keyword) {
    return AlertDto.builder()
        .categoryId(categoryId)
        .keyword(keyword)
        .build();
  }

}
