package com.mangmuganae.eolmago.domain.dealpost.entity;

import com.mangmuganae.eolmago.domain.category.entity.Category;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
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


}