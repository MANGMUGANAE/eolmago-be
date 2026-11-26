package com.mangmuganae.eolmago.domain.comment.entity;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import com.mangmuganae.eolmago.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  private Boolean selected;

  private LocalDateTime selectedAt;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_post_id")
  private DealPost dealPost;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @Builder
  public Comment(Long id, String content, Boolean selected,
      LocalDateTime selectedAt, User user, DealPost dealPost,
      Comment comment) {
    this.id = id;
    this.content = content;
    this.selected = selected;
    this.selectedAt = selectedAt;
    this.user = user;
    this.dealPost = dealPost;
    this.comment = comment;
  }

  public static Comment of(String content, Boolean selected,
      LocalDateTime selectedAt, User user, DealPost dealPost,
      Comment comment) {

    return Comment.builder()
        .id(null)
        .content(content)
        .selected(selected)
        .selectedAt(selectedAt)
        .user(user)
        .dealPost(dealPost)
        .comment(comment)
        .build();
  }


}
