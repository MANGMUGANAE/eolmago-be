package com.mangmuganae.eolmago.domain.dealpost.entity;

import com.mangmuganae.eolmago.domain.dealpost.exception.InvalidDealPostSortTypeException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;
import org.springframework.data.domain.Sort;

public enum DealPostSortType {

  LATEST("createdAt", Sort.Direction.DESC), //최신순 : 최근에 올라온 글 먼저
  PRICE("productPrice", Sort.Direction.ASC), //가격순 : 저렴한 상품 먼저
  GRADE("user.jjandolRankLevel", Sort.Direction.DESC); //등급순 : 업로드자의 짠돌 등급이 높은 글 먼저

  private final String fieldName;
  private final Sort.Direction direction;

  DealPostSortType(String fieldName, Sort.Direction direction) {
    this.fieldName = fieldName;
    this.direction = direction;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Sort.Direction getDirection() {
    return direction;
  }

  /**
   * 요청 파라미터 문자열을 정렬 조건으로 변환합니다. 대소문자는 구분하지 않습니다.
   * 값이 없으면 기본값인 최신순으로, 정의되지 않은 값이면 400 으로 응답됩니다.
   */
  public static DealPostSortType from(String value) {

    if (value == null || value.isBlank()) {
      return LATEST;
    }

    for (DealPostSortType sortType : values()) {
      if (sortType.name().equalsIgnoreCase(value.trim())) {
        return sortType;
      }
    }

    throw new InvalidDealPostSortTypeException(ErrorCode.INVALID_DEAL_POST_SORT_TYPE);
  }

  /**
   * 정렬 기준값이 같을 때 순서가 뒤바뀌지 않도록 id 를 보조 정렬 조건으로 함께 사용합니다.
   */
  public Sort toSort() {

    if (this == LATEST) {
      return Sort.by(direction, fieldName).and(Sort.by(Sort.Direction.DESC, "id"));
    }

    return Sort.by(direction, fieldName)
        .and(Sort.by(Sort.Direction.DESC, "createdAt"))
        .and(Sort.by(Sort.Direction.DESC, "id"));
  }

}
