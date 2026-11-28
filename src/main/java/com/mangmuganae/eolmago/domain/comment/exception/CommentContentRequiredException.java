package com.mangmuganae.eolmago.domain.comment.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CommentContentRequiredException extends CustomException {

  public CommentContentRequiredException(ErrorCode errorCode) {
    super(errorCode);
  }
}
