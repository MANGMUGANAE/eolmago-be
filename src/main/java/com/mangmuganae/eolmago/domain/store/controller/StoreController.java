package com.mangmuganae.eolmago.domain.store.controller;

import com.mangmuganae.eolmago.domain.store.dto.StoreDto;
import com.mangmuganae.eolmago.domain.store.dto.StoreResponseDto;
import com.mangmuganae.eolmago.domain.store.dto.StoreSearchRequestDto;
import com.mangmuganae.eolmago.domain.store.service.StoreService;
import com.mangmuganae.eolmago.global.common.response.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stores")
public class StoreController {

  private final StoreService storeService;

  @PostMapping("/search")
  public ResponseDTO<StoreDto> searchByPurchaseLink(
      @RequestBody StoreSearchRequestDto storeSearchRequestDto) {

    StoreDto storeDto = storeService
        .searchByPurchaseLink(storeSearchRequestDto.getPurchaseLink());

    return ResponseDTO.of(storeDto, "판매처 조회에 성공하였습니다.");
  }

  @GetMapping
  public ResponseDTO<StoreResponseDto> searchByStoreName(
      @RequestParam(value = "keyword", required = false) String keyword) {

    StoreResponseDto storeResponseDto = storeService.searchByStoreName(keyword);

    return ResponseDTO.of(storeResponseDto, "판매처 검색에 성공하였습니다.");
  }

}
