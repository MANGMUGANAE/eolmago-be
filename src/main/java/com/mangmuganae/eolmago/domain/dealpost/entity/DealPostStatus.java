package com.mangmuganae.eolmago.domain.dealpost.entity;

public enum DealPostStatus {
  HOLD, //검증 대기 (업로드 직후 기본 상태)
  ACTIVE, //3단계 검증을 모두 통과한 상태
  ENDED, //종료된 핫딜
  REJECTED //운영자가 거절한 상태
}
