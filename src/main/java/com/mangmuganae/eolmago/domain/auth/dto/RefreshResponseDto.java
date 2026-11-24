package com.mangmuganae.eolmago.domain.auth.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshResponseDto {

  private String accessToken;
  private String refreshToken;

  @Builder
  private RefreshResponseDto(String accessToken, String refreshToken) {

    this.accessToken = accessToken;
    this.refreshToken = refreshToken;

  }

  public static RefreshResponseDto of(String accessToken, String refreshToken) {

    return RefreshResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();

  }

}
