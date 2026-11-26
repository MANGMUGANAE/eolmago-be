package com.mangmuganae.eolmago.domain.user.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class NicknameRequiredException extends CustomException {

  public NicknameRequiredException(ErrorCode errorCode) {
    super(errorCode);
  }
}
