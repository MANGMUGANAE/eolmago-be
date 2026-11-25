package com.mangmuganae.eolmago.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreDto {

  private Long storeId;
  private String storeName;

  @Builder
  private StoreDto(Long storeId, String storeName) {
    this.storeId = storeId;
    this.storeName = storeName;
  }

  public static StoreDto of(Long storeId, String storeName) {
    return StoreDto.builder()
        .storeId(storeId)
        .storeName(storeName)
        .build();
  }

}
