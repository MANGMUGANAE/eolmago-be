package com.mangmuganae.eolmago.domain.reportpost.entity;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReportPost {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Lob
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_post_id")
  private DealPost dealPost;

  // TODO: jjandol_post_id 추가해야함

  @Builder
  private ReportPost(Long id, String content, User user, DealPost dealPost) {
    this.id = id;
    this.content = content;
    this.user = user;
    this.dealPost = dealPost;
  }

  public static ReportPost of(String content, User user, DealPost dealPost) {
    return ReportPost.builder()
        .id(null)
        .content(content)
        .user(user)
        .dealPost(dealPost)
        .build();
  }

}
