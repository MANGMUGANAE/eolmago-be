package com.mangmuganae.eolmago.domain.category.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
@Getter
public class CategoryResponseDto {

  private List<CategoryDto> categories;

  @Builder
  private CategoryResponseDto(List<CategoryDto> categories) {
    this.categories = categories;
  }


  public static CategoryResponseDto of(List<CategoryDto> categories) {
    return CategoryResponseDto.builder()
        .categories(categories)
        .build();
  }



}
