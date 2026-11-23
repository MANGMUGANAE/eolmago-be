package com.mangmuganae.eolmago.global.security.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

  public UserNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
