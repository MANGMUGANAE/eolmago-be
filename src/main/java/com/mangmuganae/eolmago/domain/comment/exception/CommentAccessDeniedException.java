package com.mangmuganae.eolmago.domain.comment.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CommentAccessDeniedException extends CustomException {

  public CommentAccessDeniedException(ErrorCode errorCode) {
    super(errorCode);
  }
}
