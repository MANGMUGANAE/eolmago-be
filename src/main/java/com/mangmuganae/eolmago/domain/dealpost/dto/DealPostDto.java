package com.mangmuganae.eolmago.domain.dealpost.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPostDto {

  private String purchaseLink;
  private String productName;
  private Integer productPrice;

  private Long categoryId;
  private Long storeId;
  private String content;

  @Builder
  private DealPostDto(String purchaseLink, String productName, Integer productPrice,
      Long categoryId, Long storeId, String content) {

    this.purchaseLink = purchaseLink;
    this.productName = productName;
    this.productPrice = productPrice;

    this.categoryId = categoryId;
    this.storeId = storeId;
    this.content = content;
  }

}
