package com.mangmuganae.eolmago.domain.dealpostreaction.entity;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import jakarta.persistence.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "deal_post_reaction")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DealPostReaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private ReactionType reactionType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_post_id")
  private DealPost dealPost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Builder
  private DealPostReaction(Long id, ReactionType reactionType,
      DealPost dealPost, User user) {
    this.id = id;
    this.reactionType = reactionType;
    this.dealPost = dealPost;
    this.user = user;
  }

  public static DealPostReaction like(User user, DealPost dealPost) {
    return DealPostReaction.builder()
        .id(null)
        .reactionType(ReactionType.LIKE)
        .dealPost(dealPost)
        .user(user)
        .build();
  }

  public static DealPostReaction dislike(User user, DealPost dealPost) {
    return DealPostReaction.builder()
        .id(null)
        .reactionType(ReactionType.DISLIKE)
        .dealPost(dealPost)
        .user(user)
        .build();
  }


}
