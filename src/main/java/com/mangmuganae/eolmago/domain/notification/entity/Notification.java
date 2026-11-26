package com.mangmuganae.eolmago.domain.notification.entity;

import com.mangmuganae.eolmago.domain.dealpost.entity.DealPost;
import com.mangmuganae.eolmago.domain.user.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  private NotificationType notificationType;
  /*
  COMMENT : 댓글 관련 알림임
  ZZAN_PICK : 댓글이 짠픽으로 선정된 경우 알림임
  KEYWORD : 키워드 알림 설정한 핫딜글 알림
  CATEGORY : 카테고리 알림 설정한 핫딜글 알림
   */

  private Boolean read;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "deal_post_id")
  private DealPost dealPost;



}
