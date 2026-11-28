package com.mangmuganae.eolmago.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CommentCreateResponseDto {

  private Long commentId;
  private Long parentCommentId;

  @Builder
  private CommentCreateResponseDto(Long commentId, Long parentCommentId) {
    this.commentId = commentId;
    this.parentCommentId = parentCommentId;
  }

  public static CommentCreateResponseDto of(Long commentId, Long parentCommentId) {
    return CommentCreateResponseDto.builder()
        .commentId(commentId)
        .parentCommentId(parentCommentId)
        .build();
  }

}
