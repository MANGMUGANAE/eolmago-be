package com.mangmuganae.eolmago.domain.category.service;

import com.mangmuganae.eolmago.domain.category.dto.CategoryResponseDto;
import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;


  public CategoryResponseDto findAll() {

    List<Category> categories = categoryRepository.findAll();

    CategoryResponseDto categoryResponseDto = CategoryResponseDto.of(
        categories.stream()
            .map(Category::toDto)
            .toList()
    );

    return categoryResponseDto;

  }
}
