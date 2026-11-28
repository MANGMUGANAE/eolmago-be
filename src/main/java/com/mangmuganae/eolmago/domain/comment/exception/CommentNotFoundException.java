package com.mangmuganae.eolmago.domain.comment.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CommentNotFoundException extends CustomException {

  public CommentNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
