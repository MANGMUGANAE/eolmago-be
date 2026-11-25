package com.mangmuganae.eolmago.domain.store.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class StoreResponseDto {

  private List<StoreDto> stores;

  @Builder
  private StoreResponseDto(List<StoreDto> stores) {
    this.stores = stores;
  }

  public static StoreResponseDto of(List<StoreDto> stores) {
    return StoreResponseDto.builder()
        .stores(stores)
        .build();
  }

}
