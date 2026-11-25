package com.mangmuganae.eolmago.domain.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryDto {

  private Long categoryId;
  private String categoryName;

  @Builder
  private CategoryDto(Long categoryId, String categoryName) {
    this.categoryId = categoryId;
    this.categoryName = categoryName;
  }

  public static CategoryDto of(String categoryName) {
    return CategoryDto.builder()
        .categoryId(null)
        .categoryName(categoryName)
        .build();
  }

}
