package com.mangmuganae.eolmago.domain.comment.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CommentReplyDepthExceededException extends CustomException {

  public CommentReplyDepthExceededException(ErrorCode errorCode) {
    super(errorCode);
  }
}
