package com.mangmuganae.eolmago.domain.category.controller;

import com.mangmuganae.eolmago.domain.category.dto.CategoryResponseDto;
import com.mangmuganae.eolmago.domain.category.service.CategoryService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

  private final CategoryService categoryService;

  @GetMapping
  public ResponseDTO<CategoryResponseDto> findAll() {

    CategoryResponseDto categoryResponseDto = categoryService.findAll();

    return ResponseDTO.of(categoryResponseDto, "카테고리 조회에 성공하였습니다.");

  }
}
