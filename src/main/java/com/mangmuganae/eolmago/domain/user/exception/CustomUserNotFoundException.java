package com.mangmuganae.eolmago.domain.user.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class CustomUserNotFoundException extends CustomException {

  public CustomUserNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
