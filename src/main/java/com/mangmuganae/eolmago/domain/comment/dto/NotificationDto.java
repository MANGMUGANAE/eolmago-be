package com.mangmuganae.eolmago.domain.comment.dto;

import com.mangmuganae.eolmago.domain.notification.entity.NotificationType;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationDto {

  private Long notificationId;
  private String content;
  private NotificationType notificationType;
  /*
  COMMENT : 댓글 관련 알림임
  ZZAN_PICK : 댓글이 짠픽으로 선정된 경우 알림임
  KEYWORD : 키워드 알림 설정한 핫딜글 알림
  CATEGORY : 카테고리 알림 설정한 핫딜글 알림
   */

  private Boolean readYn;

  private Long dealPostId;
  private Long commentId;
  private LocalDateTime pushedAt;

  @Builder
  private NotificationDto(Long notificationId, String content,
      NotificationType notificationType, Boolean readYn, Long dealPostId,
      Long commentId, LocalDateTime pushedAt) {

    this.notificationId = notificationId;
    this.content = content;
    this.notificationType = notificationType;
    this.readYn = readYn;
    this.dealPostId = dealPostId;
    this.commentId = commentId;
    this.pushedAt = pushedAt;
  }

  public static NotificationDto of(Long notificationId, String content, NotificationType notificationType,
      Boolean readYn, Long dealPostId, Long commentId, LocalDateTime pushedAt) {

    return NotificationDto.builder()
        .notificationId(notificationId)
        .content(content)
        .notificationType(notificationType)
        .readYn(readYn)
        .dealPostId(dealPostId)
        .commentId(commentId)
        .pushedAt(pushedAt)
        .build();
  }


}
