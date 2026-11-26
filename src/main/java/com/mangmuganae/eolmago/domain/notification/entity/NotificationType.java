package com.mangmuganae.eolmago.domain.notification.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = """
    알림 종류입니다.
    - COMMENT : 댓글 관련 알림
    - ZZAN_PICK : 댓글이 짠픽으로 선정된 알림
    - KEYWORD : 키워드 알림을 설정한 핫딜 글 알림
    - CATEGORY : 카테고리 알림을 설정한 핫딜 글 알림
    """)
public enum NotificationType {
  COMMENT, ZZAN_PICK, KEYWORD, CATEGORY
}
