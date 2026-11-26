package com.mangmuganae.eolmago.domain.dealpost.entity;

import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.comment.entity.Comment;
import com.mangmuganae.eolmago.domain.notification.entity.Notification;
import com.mangmuganae.eolmago.domain.store.entity.Store;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "deal_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPost extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String purchaseLink;
  private String productName;
  private Integer productPrice;

  @Lob
  private String content;

  private Long likeCount;
  private Long dislikeCount;
  private Long viewCount;
  private Integer commentCount;

  @Enumerated(EnumType.STRING)
  private DealPostStatus dealPostStatus;

  private Integer dealReliabilityScore;

  private Boolean linkVerified; //1단계 : 제휴/리워드 링크 여부 검증 통과
  private Boolean reliabilityVerified; //2단계 : 업로드자 신뢰도 검증 통과
  private Boolean advertisementVerified; //3단계 : 광고글 여부 검증 통과

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id")
  private Store store;


  @Builder
  private DealPost(Long id, String purchaseLink, String productName, Integer productPrice,
      String content, Long likeCount, Long dislikeCount, Long viewCount, Integer commentCount,
      DealPostStatus dealPostStatus, Integer dealReliabilityScore,
      Boolean linkVerified, Boolean reliabilityVerified, Boolean advertisementVerified,
      User user, Category category, Store store) {

    this.id = id;
    this.purchaseLink = purchaseLink;
    this.productName = productName;
    this.productPrice = productPrice;

    this.content = content;
    this.likeCount = likeCount;
    this.dislikeCount = dislikeCount;
    this.viewCount = viewCount;
    this.commentCount = commentCount;

    this.dealPostStatus = dealPostStatus;
    this.dealReliabilityScore = dealReliabilityScore;

    this.linkVerified = linkVerified;
    this.reliabilityVerified = reliabilityVerified;
    this.advertisementVerified = advertisementVerified;

    this.user = user;
    this.category = category;
    this.store = store;
  }

  public static DealPost createHold(String purchaseLink, String productName, Integer productPrice,
      String content, User user, Category category, Store store) {

    return DealPost.builder()
        .id(null)
        .purchaseLink(purchaseLink)
        .productName(productName)
        .productPrice(productPrice)

        .content(content)
        .likeCount(0L)
        .dislikeCount(0L)
        .viewCount(0L)
        .commentCount(0)

        .dealPostStatus(DealPostStatus.HOLD)
        .dealReliabilityScore(0)

        .linkVerified(false)
        .reliabilityVerified(false)
        .advertisementVerified(false)

        .user(user)
        .category(category)
        .store(store)
        .build();
  }

  public void passLinkVerification() {
    this.linkVerified = true;
    activateIfAllVerified();
  }

  public void passReliabilityVerification() {
    this.reliabilityVerified = true;
    activateIfAllVerified();
  }

  public void passAdvertisementVerification() {
    this.advertisementVerified = true;
    activateIfAllVerified();
  }

  public boolean isOwnedBy(User user) {
    return this.user.getId().equals(user.getId());
  }

  private void activateIfAllVerified() {
    if (linkVerified && reliabilityVerified && advertisementVerified) {
      this.dealPostStatus = DealPostStatus.ACTIVE;
    }
  }

}
