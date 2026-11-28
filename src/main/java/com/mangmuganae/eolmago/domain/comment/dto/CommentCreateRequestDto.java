package com.mangmuganae.eolmago.domain.comment.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateRequestDto {

  private String content;
  private Long parentCommentId;

  @Builder
  private CommentCreateRequestDto(String content, Long parentCommentId) {
    this.content = content;
    this.parentCommentId = parentCommentId;
  }

  public static CommentCreateRequestDto of(String content, Long parentCommentId) {
    return CommentCreateRequestDto.builder()
        .content(content)
        .parentCommentId(parentCommentId)
        .build();
  }

}
