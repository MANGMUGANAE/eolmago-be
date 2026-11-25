package com.mangmuganae.eolmago.domain.category.entity;


import com.mangmuganae.eolmago.domain.category.dto.CategoryDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "category")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String categoryName;

  @Builder
  private Category(Long id, String categoryName) {
    this.id = id;
    this.categoryName = categoryName;
  }

  public CategoryDto toDto() {
    return CategoryDto.builder()
        .id(this.id)
        .categoryName(this.categoryName)
        .build();
  }

  public static Category of(String categoryName) {
    return Category.builder()
        .id(null)
        .categoryName(categoryName)
        .build();
  }

}
