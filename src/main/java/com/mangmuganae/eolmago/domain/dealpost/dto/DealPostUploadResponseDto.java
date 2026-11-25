package com.mangmuganae.eolmago.domain.dealpost.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DealPostUploadResponseDto {

  private Long dealPostId;

  @Builder
  private DealPostUploadResponseDto(Long dealPostId) {
    this.dealPostId = dealPostId;
  }

  public static DealPostUploadResponseDto of(Long dealPostId) {
    return DealPostUploadResponseDto.builder()
        .dealPostId(dealPostId)
        .build();
  }

}
