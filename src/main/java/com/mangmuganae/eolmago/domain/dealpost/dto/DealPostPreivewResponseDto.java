package com.mangmuganae.eolmago.domain.dealpost.dto;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.dealpost.entity.DealPostStatus;
import com.mangmuganae.eolmago.domain.user.entity.JjandolRank;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DealPostPreivewResponseDto {

  private Long dealPostId;

  private String storeName; //판매처
  private String productName; //글 제목
  private Integer productPrice; //글 가격

  private Integer commentCount; //댓글수
  private Long likeCount; //추천수
  private Long viewCount; //조회수

  private JjandolRank jjandolRank; //업로드자 짠돌 등급
  private DealPostStatus dealPostStatus; //ACTIVE(진행중) / ENDED(종료)

  private String thumbnailImageUrl; //가장 먼저 등록된 이미지
  private LocalDateTime createdAt; //글 업로드 일자

  @Builder
  private DealPostPreivewResponseDto(Long dealPostId, String storeName, String productName,
      Integer productPrice, Integer commentCount, Long likeCount, Long viewCount,
      JjandolRank jjandolRank, DealPostStatus dealPostStatus, String thumbnailImageUrl,
      LocalDateTime createdAt) {

    this.dealPostId = dealPostId;
    this.storeName = storeName;
    this.productName = productName;
    this.productPrice = productPrice;
    this.commentCount = commentCount;
    this.likeCount = likeCount;
    this.viewCount = viewCount;
    this.jjandolRank = jjandolRank;
    this.dealPostStatus = dealPostStatus;
    this.thumbnailImageUrl = thumbnailImageUrl;
    this.createdAt = createdAt;
  }

  /**
   * 판매처를 선택하지 않고 올린 글이 있으므로 storeName 은 null 일 수 있습니다.
   */
  public static DealPostPreivewResponseDto of(DealPost dealPost, String thumbnailImageUrl) {

    return DealPostPreivewResponseDto.builder()
        .dealPostId(dealPost.getId())
        .storeName(dealPost.getStore() == null ? null : dealPost.getStore().getStoreName())
        .productName(dealPost.getProductName())
        .productPrice(dealPost.getProductPrice())
        .commentCount(dealPost.getCommentCount())
        .likeCount(dealPost.getLikeCount())
        .viewCount(dealPost.getViewCount())
        .jjandolRank(dealPost.getUser() == null ? null : dealPost.getUser().getJjandolRank())
        .dealPostStatus(dealPost.getDealPostStatus())
        .thumbnailImageUrl(thumbnailImageUrl)
        .createdAt(dealPost.getCreatedAt())
        .build();
  }

}
