package com.mangmuganae.eolmago.domain.category.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CategoryDto {

  private Long id;
  private String categoryName;

  @Builder
  private CategoryDto(Long id, String categoryName) {
    this.id = id;
    this.categoryName = categoryName;
  }

  public static CategoryDto of(String categoryName) {
    return CategoryDto.builder()
        .id(null)
        .categoryName(categoryName)
        .build();
  }

}
