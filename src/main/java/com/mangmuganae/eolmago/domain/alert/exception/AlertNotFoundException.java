package com.mangmuganae.eolmago.domain.alert.exception;

import com.mangmuganae.eolmago.global.exception.CustomException;
import com.mangmuganae.eolmago.global.exception.ErrorCode;

public class AlertNotFoundException extends CustomException {

  public AlertNotFoundException(ErrorCode errorCode) {
    super(errorCode);
  }
}
