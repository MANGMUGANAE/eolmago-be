package com.mangmuganae.eolmago.domain.notification.entity;

import com.mangmuganae.eolmago.domain.comment.dto.NotificationDto;
import com.mangmuganae.eolmago.domain.comment.entity.Comment;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  private NotificationType notificationType;
  private Boolean readYn;

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
  private Notification(Long id, String content,
      NotificationType notificationType, Boolean readYn,
      User user, DealPost dealPost, Comment comment) {

    this.id = id;
    this.content = content;
    this.notificationType = notificationType;
    this.readYn = readYn;
    this.user = user;
    this.dealPost = dealPost;
    this.comment =  comment;
  }

  public static Notification of(String content, NotificationType notificationType,
      User user, DealPost dealPost, Comment comment) {

    return Notification.builder()
        .id(null)
        .content(content)
        .notificationType(notificationType)
        .readYn(false)
        .user(user)
        .dealPost(dealPost)
        .comment(comment)
        .build();
  }

  public NotificationDto toDto() {
    return NotificationDto.builder()
        .notificationId(this.id)
        .content(this.content)
        .notificationType(this.notificationType)
        .readYn(this.readYn)
        .dealPostId(this.dealPost.getId())
        .commentId(this.comment.getId())
        .pushedAt(this.getCreatedAt())
        .build();
  }



}
