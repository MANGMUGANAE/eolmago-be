package com.mangmuganae.eolmago.domain.store.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoreSearchRequestDto {

  private String purchaseLink;

}
