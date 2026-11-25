package com.mangmuganae.eolmago.domain.dealpost.entity;

import com.mangmuganae.eolmago.domain.category.entity.Category;
import com.mangmuganae.eolmago.domain.store.entity.Store;
import com.mangmuganae.eolmago.domain.user.entity.User;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "deal_post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPost {

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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", unique = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", unique = true)
  private Category category;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", unique = true)
  private Store store;

  @Builder
  private DealPost(Long id, String purchaseLink, String productName, Integer productPrice,
      String content, Long likeCount, Long dislikeCount, Long viewCount, Integer commentCount,
      DealPostStatus dealPostStatus, Integer dealReliabilityScore,
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

    this.user = user;
    this.category = category;
    this.store = store;
  }

  public static DealPost of(String purchaseLink, String productName, Integer productPrice,
      String content, Long likeCount, Long dislikeCount, Long viewCount, Integer commentCount,
      DealPostStatus dealPostStatus, Integer dealReliabilityScore,
      User user, Category category, Store store) {

    return DealPost.builder()
        .id(null)
        .purchaseLink(purchaseLink)
        .productName(productName)
        .productPrice(productPrice)

        .content(content)
        .likeCount(likeCount)
        .dislikeCount(dislikeCount)
        .viewCount(viewCount)
        .commentCount(commentCount)

        .dealPostStatus(dealPostStatus)
        .dealReliabilityScore(dealReliabilityScore)

        .user(user)
        .category(category)
        .store(store)
        .build();
  }


}