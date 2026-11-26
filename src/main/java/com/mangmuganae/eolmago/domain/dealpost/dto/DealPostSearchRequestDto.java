package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostSortType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter //쿼리 파라미터 바인딩(@ParameterObject)은 setter 를 사용합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPostSearchRequestDto {

  private static final int DEFAULT_PAGE = 0;
  private static final int DEFAULT_SIZE = 20;
  private static final int MAX_SIZE = 50;

  private Long category;

  private String keyword;

  private Boolean ended;
  private Boolean hotDeal;

  //latest / price / grade 중 하나입니다. 대소문자는 구분하지 않습니다.
  private String sortType;

  private Integer page;
  private Integer size;

  /**
   * 정렬 조건을 꺼냅니다. 보내지 않으면 최신순이고, 정의되지 않은 값이면 400 으로 응답됩니다.
   */
  public DealPostSortType getSortType() {
    return DealPostSortType.from(sortType);
  }

  /**
   * true 면 종료된 핫딜을 결과에서 제외합니다. 기본값은 false 로 진행중 / 종료 전부 조회합니다.
   */
  public boolean isEndedExcluded() {
    return Boolean.TRUE.equals(ended);
  }

  /**
   * true 면 핫딜 필터가 적용되어 업로드자 등급과 핫딜 신뢰도 조건을 함께 만족하는 글만 조회합니다.
   */
  public boolean isHotDealOnly() {
    return Boolean.TRUE.equals(hotDeal);
  }

  public int getPage() {
    return (page == null || page < 0) ? DEFAULT_PAGE : page;
  }

  public int getSize() {
    if (size == null || size < 1) {
      return DEFAULT_SIZE;
    }

    return Math.min(size, MAX_SIZE);
  }

}
