package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DealPostVerificationResponseDto {

  private Long dealPostId;
  private boolean passed;
  private DealPostStatus dealPostStatus;

  @Builder
  private DealPostVerificationResponseDto(Long dealPostId, boolean passed,
      DealPostStatus dealPostStatus) {
    this.dealPostId = dealPostId;
    this.passed = passed;
    this.dealPostStatus = dealPostStatus;
  }

  public static DealPostVerificationResponseDto of(Long dealPostId, boolean passed,
      DealPostStatus dealPostStatus) {
    return DealPostVerificationResponseDto.builder()
        .dealPostId(dealPostId)
        .passed(passed)
        .dealPostStatus(dealPostStatus)
        .build();
  }

}
